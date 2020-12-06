package com.github.ep2p.eleuth.config;

import com.github.ep2p.encore.helper.KeyStoreWrapper;
import lombok.SneakyThrows;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
public class SSLConfig {
    private final KeyStoreWrapper keyStoreWrapper;

    public SSLConfig(KeyStoreWrapper keyStoreWrapper) {
        this.keyStoreWrapper = keyStoreWrapper;
    }

    @Bean
    public SSLContext sslContext() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return new SSLContextBuilder()
                .loadTrustMaterial(
                        keyStoreWrapper.getKeyStore()
                        , new TrustStrategy() {
                            @SneakyThrows
                            @Override
                            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                for (X509Certificate x509Certificate : x509Certificates) {
                                    if (keyStoreWrapper.getCertificatesList().contains(x509Certificate)) {
                                        return true;
                                    }
                                }
                                return false; //todo: might wanna return true but check if certificate public key matches eleuth challanges
                            }
                        }
                ).build();
    }
}
