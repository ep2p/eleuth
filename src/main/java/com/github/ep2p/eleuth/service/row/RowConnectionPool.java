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
import lab.idioglossia.row.cs.SpringReuseRowClientFactory;
import lab.idioglossia.row.server.context.RowUser;
import lab.idioglossia.row.server.repository.RowSessionRegistry;
import lab.idioglossia.row.server.ws.RowServerWebsocket;
import lab.idioglossia.row.server.ws.SpringRowServerWebsocket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import javax.net.ssl.SSLContext;
import javax.websocket.CloseReason;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//provides RowClient based on nodeId, provides api to register clients based on their existing connections
@Service
@Slf4j
public class RowConnectionPool {
    private final RowClientFactory rowClientFactory;
    private final SSLContext sslContext;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final RowSessionRegistry rowSessionRegistry;
    private final SpringReuseRowClientFactory springReuseRowClientFactory;
    private final Map<String, RowClient> pool = new ConcurrentHashMap<>();
    private final Map<String, RowUser> nodeMap = new ConcurrentHashMap<>();
    private final Listener listener = new Listener();

    @Autowired
    public RowConnectionPool(RowClientFactory rowClientFactory, SSLContext sslContext, ObjectMapper objectMapper, RowSessionRegistry rowSessionRegistry, SpringReuseRowClientFactory springReuseRowClientFactory) {
        this.rowClientFactory = rowClientFactory;
        this.sslContext = sslContext;
        this.objectMapper = objectMapper;
        this.rowSessionRegistry = rowSessionRegistry;
        this.springReuseRowClientFactory = springReuseRowClientFactory;
    }

    public synchronized void registerRowUser(String nodeId, RowUser rowUser){
        nodeMap.put(nodeId, rowUser);
    }

    public synchronized void unRegister(RowUser rowUser){
        nodeMap.forEach((s, rowUser1) -> {
            if(rowUser1.equals(rowUser)){
                nodeMap.remove(s);
            }
        });
    }

    public synchronized void unRegister(String nodeId){
        nodeMap.remove(nodeId);
    }

    public synchronized RowClient getClient(String nodeId, ROWConnectionInfo rowConnectionInfo){
        RowClient rowClient = pool.get(nodeId);
        if(rowClient != null)
            return rowClient;

        if(nodeMap.containsKey(nodeId)){
            RowServerWebsocket<?> rowSession = getRowServerWSSession(nodeId);
            if(rowSession != null){
                RowClient reuseRowClient = getReuseClient(rowSession);
                pool.put(nodeId, rowClient);
                return reuseRowClient;
            }
        }

        rowClient = getClient(rowConnectionInfo);
        pool.put(rowConnectionInfo.getFullAddress(), rowClient);
        return rowClient;
    }

    private RowClient getClient(ROWConnectionInfo rowConnectionInfo) {
        RowClientConfig<RowWebsocketSession> rowClientConfig = rowClientFactory.getRowClientConfig();
        configure(rowClientConfig);
        rowClientConfig.setAddress(rowConnectionInfo.getFullAddress());
        RestTemplateRowHttpClient restTemplateRowHttpClient = new RestTemplateRowHttpClient(rowConnectionInfo.getHttpAddress(), restTemplate, objectMapper);
        RowClient rowClient = rowClientFactory.getRowClient(rowClientConfig, restTemplateRowHttpClient);
        rowClient.open();
        return rowClient;
    }

    private RowClient getReuseClient(RowServerWebsocket<?> rowSession) {
        WebSocketSession webSocketSession = rowSession.getNativeSession(WebSocketSession.class);
        SpringRowServerWebsocket springRowServerWebsocket = new SpringRowServerWebsocket(webSocketSession);
        RowClientConfig rowClientConfig = springReuseRowClientFactory.getRowClientConfig();
        configure(rowClientConfig);
        return springReuseRowClientFactory.getRowClient(rowClientConfig, springRowServerWebsocket);
    }

    private RowServerWebsocket<?> getRowServerWSSession(String nodeId) {
        RowUser rowUser = nodeMap.get(nodeId);
        return rowSessionRegistry.getSession(rowUser.getUserId(), rowUser.getSessionId());
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
