package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.ep2p.eleuth.config.serialization.ExternalNodeModule;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.service.KeyService;
import com.github.ep2p.eleuth.service.KeyStoreService;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.CNGenerator;
import com.github.ep2p.encore.key.PubHashUserId128Generator;
import com.github.ep2p.encore.key.UserIdGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.math.BigInteger;
import java.security.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Configuration
@Log4j2
@EnableConfigurationProperties({ConfigProperties.class})
public class EleuthConfiguration {
    private final ConfigProperties configProperties;

    @Autowired
    public EleuthConfiguration(ConfigProperties configProperties) {
        this.configProperties = configProperties;
        log.info("Configuration: "+ configProperties.toString());
    }

    @ConditionalOnMissingBean(ObjectMapper.class)
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        objectMapper.registerModules(new ExternalNodeModule());
        return objectMapper;
    }

    @Bean("userIdGenerator")
    public UserIdGenerator<BigInteger> userIdGenerator(){
        return new PubHashUserId128Generator();
    }

    @Bean
    public CNGenerator cnGenerator(){
        return new CNGenerator() {
            @Override
            public String generate() {
                return "cn="+ configProperties.getCn();
            }
        };
    }

    @Bean
    @DependsOn("userIdGenerator")
    public KeyService keyService(UserIdGenerator<BigInteger> userIdGenerator){
        return new KeyService(configProperties, userIdGenerator);
    }

    @Bean
    @DependsOn({"keyService", "cnGenerator"})
    public KeyStoreService keyStoreService(KeyService keyService, CNGenerator cnGenerator){
        return new KeyStoreService(configProperties, keyService, cnGenerator);
    }

    @Bean
    @DependsOn("keyStoreService")
    public KeyStoreWrapper keyStoreWrapper(KeyStoreService keyStoreService){
        KeyStore keyStore = keyStoreService.generateKeyStore();
        return new KeyStoreWrapper(keyStore, keyStoreService.getKeystoreAddress(), configProperties.getKeyStorePass());
    }

    @Bean
    @DependsOn({"keyStoreWrapper", "userIdGenerator"})
    public NodeInformation nodeInformation(KeyStoreWrapper keyStoreWrapper, UserIdGenerator<BigInteger> userIdGenerator) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        KeyPair keyPair = keyStoreWrapper.getMainKeyPair();
        BigInteger nodeId = userIdGenerator.generate(keyPair.getPublic());
        log.info("Node ID: " + nodeId);
        return NodeInformation.builder()
                .id(nodeId)
                .keyPair(keyPair)
                .build();
    }
}
