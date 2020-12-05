package com.github.ep2p.eleuth.service.row;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import lab.idioglossia.row.client.RestTemplateRowHttpClient;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.RowClientFactory;
import lab.idioglossia.row.client.callback.RowTransportListener;
import lab.idioglossia.row.client.tyrus.RowClientConfig;
import lab.idioglossia.row.client.util.DefaultJacksonMessageConverter;
import lab.idioglossia.row.client.ws.RowWebsocketSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.glassfish.tyrus.client.SslContextConfigurator;
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.websocket.CloseReason;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RowConnectionPool {
    private final RowClientFactory rowClientFactory;
    private final KeyStoreWrapper keyStoreWrapper;
    private final ObjectMapper objectMapper;
    private final Map<String, RowClient> pool = new ConcurrentHashMap<>();
    private final Listener listener = new Listener();

    @Autowired
    public RowConnectionPool(RowClientFactory rowClientFactory, KeyStoreWrapper keyStoreWrapper, ObjectMapper objectMapper) {
        this.rowClientFactory = rowClientFactory;
        this.keyStoreWrapper = keyStoreWrapper;
        this.objectMapper = objectMapper;
    }

    public synchronized RowClient getClient(ROWConnectionInfo rowConnectionInfo){
        if(pool.containsKey(rowConnectionInfo.getFullAddress())){
            return pool.get(rowConnectionInfo.getFullAddress());
        }else {
            System.out.println(rowConnectionInfo.getFullAddress());
            RowClientConfig<RowWebsocketSession> rowClientConfig = rowClientFactory.getRowClientConfig();
            rowClientConfig.setRowTransportListener(listener);
            rowClientConfig.setMessageConverter(new DefaultJacksonMessageConverter(objectMapper));
            rowClientConfig.setAddress(rowConnectionInfo.getFullAddress());
            SslEngineConfigurator sslEngineConfigurator = getSslEngine();
            rowClientConfig.getWebsocketConfig().setSslEngineConfigurator(sslEngineConfigurator);
            RestTemplateRowHttpClient restTemplateRowHttpClient = new RestTemplateRowHttpClient(rowConnectionInfo.getHttpAddress(), new RestTemplate(), objectMapper);
            RowClient rowClient = rowClientFactory.getRowClient(rowClientConfig, restTemplateRowHttpClient);
            rowClient.open();
            pool.put(rowConnectionInfo.getFullAddress(), rowClient);
            return rowClient;
        }
    }

    @SneakyThrows
    private SslEngineConfigurator getSslEngine() {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                        keyStoreWrapper.getKeyStore()
                        , new TrustStrategy() {
                            @Override
                            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                System.out.println("==========");
                                for (X509Certificate x509Certificate : x509Certificates) {
                                    System.out.println(x509Certificate.getIssuerX500Principal().toString());
                                    System.out.println(x509Certificate.getIssuerAlternativeNames() != null ? x509Certificate.getIssuerAlternativeNames().toString() : "");
                                    System.out.println(new String(x509Certificate.getPublicKey().toString()));
                                }
                                System.out.println("==========");
                                System.out.println(s);
                                System.out.println("==========");
                                return true;
                            }
                        }
                ).build();
        SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(sslContext);
        sslEngineConfigurator.setHostVerificationEnabled(false);
        return sslEngineConfigurator;
    }

    private class Listener extends RowTransportListener.Default {
        @Override
        public void onOpen(RowWebsocketSession rowWebsocketSession) {

        }

        @Override
        public void onError(RowWebsocketSession rowWebsocketSession, Throwable throwable) {
            log.error("Websocket error ", throwable);
        }

        @Override
        public void onClose(RowClient rowClient, RowWebsocketSession rowWebsocketSession, CloseReason closeReason) {
            pool.remove(rowWebsocketSession.getUri().toString());
        }
    }

}
