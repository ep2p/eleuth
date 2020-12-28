package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.config.ConfigProperties;
import com.github.ep2p.eleuth.util.Path;
import com.github.ep2p.encore.key.CNGenerator;
import com.github.ep2p.encore.key.KeyStoreGenerator;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.KeyPair;
import java.security.KeyStore;

//generates a keystore to hold keys of node
@Log4j2
public class KeyStoreService {
    private final ConfigProperties configProperties;
    private final KeyService keyService;
    private final CNGenerator cnGenerator;

    private KeyStore cachedKeyStore = null;
    private String cachedKeyStoreAddress = null;

    public KeyStoreService(ConfigProperties configProperties, KeyService keyService, CNGenerator cnGenerator) {
        this.configProperties = configProperties;
        this.keyService = keyService;
        this.cnGenerator = cnGenerator;
    }

    @PostConstruct
    public void init(){
        log.debug("Initializing KeyGeneratorService");
        generateKeyStore();
    }

    public synchronized KeyStore generateKeyStore(){
        if(cachedKeyStore == null){
            KeyPair keyPair = null;
            if(!exists()){
                keyPair = keyService.generateKeyPair();
            }
            KeyStoreGenerator keyStoreGenerator = new KeyStoreGenerator();
            cachedKeyStore = keyStoreGenerator.generate(new KeyStoreGenerator.KeyStoreGeneratorInput(cnGenerator, getKeystoreAddress(), configProperties.getKeyStorePass(), keyPair));
        }
        return cachedKeyStore;
    }

    public boolean exists(){
        return new File(getKeystoreAddress()).exists();
    }

    public String getKeystoreAddress(){
        synchronized (this){
            if(cachedKeyStoreAddress == null){
                cachedKeyStoreAddress = Path.combine(configProperties.getWorkingDir(), "keystore.jks");
            }
            return cachedKeyStoreAddress;
        }
    }
}
