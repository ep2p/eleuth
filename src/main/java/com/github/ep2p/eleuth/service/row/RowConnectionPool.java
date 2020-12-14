package com.github.ep2p.eleuth.service.row;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.row.client.RestTemplateRowHttpClient;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.RowClientFactory;
import lab.idioglossia.row.client.callback.RowTransportListener;
import lab.idioglossia.row.client.tyrus.RowClientConfig;
import lab.idioglossia.row.client.util.DefaultJacksonMessageConverter;
import lab.idioglossia.row.client.ws.RowWebsocketSession;
import lab.idioglossia.row.client.ws.WebsocketSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.websocket.CloseReason;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RowConnectionPool {
    private final RowClientFactory rowClientFactory;
    private final SSLContext sslContext;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final Map<String, RowClient> pool = new ConcurrentHashMap<>();
    private final Listener listener = new Listener();

    @Autowired
    public RowConnectionPool(RowClientFactory rowClientFactory, SSLContext sslContext, ObjectMapper objectMapper) {
        this.rowClientFactory = rowClientFactory;
        this.sslContext = sslContext;
        this.objectMapper = objectMapper;
    }

    public synchronized RowClient getClient(ROWConnectionInfo rowConnectionInfo){
        if(pool.containsKey(rowConnectionInfo.getFullAddress())){
            return pool.get(rowConnectionInfo.getFullAddress());
        }else {
            System.out.println(rowConnectionInfo.getFullAddress());
            RowClientConfig<RowWebsocketSession> rowClientConfig = rowClientFactory.getRowClientConfig();
            configure(rowClientConfig);
            rowClientConfig.setAddress(rowConnectionInfo.getFullAddress());
            RestTemplateRowHttpClient restTemplateRowHttpClient = new RestTemplateRowHttpClient(rowConnectionInfo.getHttpAddress(), restTemplate, objectMapper);
            RowClient rowClient = rowClientFactory.getRowClient(rowClientConfig, restTemplateRowHttpClient);
            rowClient.open();
            pool.put(rowConnectionInfo.getFullAddress(), rowClient);
            return rowClient;
        }
    }

    private void configure(RowClientConfig<RowWebsocketSession> rowClientConfig) {
        rowClientConfig.setRowTransportListener(listener);
        rowClientConfig.setMessageConverter(new DefaultJacksonMessageConverter(objectMapper));
        SslEngineConfigurator sslEngineConfigurator = getSslEngine();
        rowClientConfig.getWebsocketConfig().setSslEngineConfigurator(sslEngineConfigurator);
    }

    @SneakyThrows
    private SslEngineConfigurator getSslEngine() {
        SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(sslContext);
        sslEngineConfigurator.setHostVerificationEnabled(false);
        return sslEngineConfigurator;
    }

    private class Listener extends RowTransportListener.Default {
        @Override
        public void onOpen(WebsocketSession rowWebsocketSession) {
            super.onOpen(rowWebsocketSession);
        }

        @Override
        public void onError(WebsocketSession rowWebsocketSession, Throwable throwable) {
            log.error("Websocket error ", throwable);
        }

        @Override
        public void onClose(RowClient rowClient, WebsocketSession rowWebsocketSession, CloseReason closeReason) {
            pool.remove(rowWebsocketSession.getUri().toString());
        }
    }

}
