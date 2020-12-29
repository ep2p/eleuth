package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.ep2p.eleuth.config.serialization.ExternalNodeModule;
import com.github.ep2p.eleuth.model.entity.RingMemberEntity;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.RingMemberRepository;
import com.github.ep2p.eleuth.service.KeyService;
import com.github.ep2p.eleuth.service.KeyStoreService;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.CNGenerator;
import com.github.ep2p.encore.key.PubHashUserId128Generator;
import com.github.ep2p.encore.key.UserIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

@Configuration
@Slf4j
@EnableConfigurationProperties({ConfigProperties.class, NodeProperties.class})
public class EleuthConfiguration {
    private final ConfigProperties configProperties;
    private final NodeProperties nodeProperties;
    private final Environment env;
    private final RingMemberRepository ringMemberRepository;

    @Autowired
    public EleuthConfiguration(ConfigProperties configProperties, NodeProperties nodeProperties, Environment env, RingMemberRepository ringMemberRepository) {
        this.configProperties = configProperties;
        this.nodeProperties = nodeProperties;
        this.env = env;
        this.ringMemberRepository = ringMemberRepository;
        log.info("Configuration: "+ configProperties.toString());
        log.info("Node Properties: "+ nodeProperties.toString());
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

    @Bean("keyStoreService")
    @DependsOn({"keyService", "cnGenerator"})
    public KeyStoreService keyStoreService(KeyService keyService, CNGenerator cnGenerator){
        return new KeyStoreService(configProperties, keyService, cnGenerator);
    }

    @Bean("keyStoreWrapper")
    @DependsOn("keyStoreService")
    public KeyStoreWrapper keyStoreWrapper(KeyStoreService keyStoreService){
        KeyStore keyStore = keyStoreService.generateKeyStore();
        return new KeyStoreWrapper(keyStore, keyStoreService.getKeystoreAddress(), configProperties.getKeyStorePass());
    }

    @Bean("rowConnectionInfo")
    @DependsOn("keyStoreWrapper")
    public ROWConnectionInfo rowConnectionInfo(KeyStoreWrapper keyStoreWrapper) throws KeyStoreException, CertificateEncodingException {
        Certificate certificate = keyStoreWrapper.getCertificate("main");

        return ROWConnectionInfo.builder()
                .ssl(Arrays.asList(env.getActiveProfiles()).contains("ssl"))
                .port(nodeProperties.getPort())
                .address(nodeProperties.getHost())
                .certificate(Base64Util.encode(certificate.getEncoded()))
                .build();
    }

    @Bean
    @DependsOn({"keyStoreWrapper", "userIdGenerator", "rowConnectionInfo"})
    public NodeInformation nodeInformation(KeyStoreWrapper keyStoreWrapper, UserIdGenerator<BigInteger> userIdGenerator, ROWConnectionInfo rowConnectionInfo) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        KeyPair keyPair = keyStoreWrapper.getMainKeyPair();
        BigInteger nodeId = userIdGenerator.generate(keyPair.getPublic());
        RingMemberEntity ringMemberEntity = this.ringMemberRepository.get();
        String ringKey = ringMemberEntity != null ? ringMemberEntity.getKey() : null;
        log.info("Node ID: " + nodeId);
        return NodeInformation.builder()
                .connectionInfo(rowConnectionInfo)
                .id(nodeId)
                .keyPair(keyPair)
                .ringKey(ringKey)
                .nodeType(configProperties.getNodeType())
                .build();
    }
}
