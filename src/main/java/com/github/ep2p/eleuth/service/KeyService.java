package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.config.ConfigProperties;
import com.github.ep2p.eleuth.config.KeyGenerationConfig;
import com.github.ep2p.encore.Generator;
import com.github.ep2p.encore.key.ChallengedKeyGeneratorDecorator;
import com.github.ep2p.encore.key.KeyGenerator;
import com.github.ep2p.encore.key.MultiThreadChallengedKeyGenerator;
import com.github.ep2p.encore.key.UserIdGenerator;
import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;
import java.security.KeyPair;

//Generates key for current node based on node type
@Log4j2
public class KeyService {
    private final ConfigProperties configProperties;
    private final UserIdGenerator<BigInteger> userIdGenerator;

    public KeyService(ConfigProperties configProperties, UserIdGenerator<BigInteger> userIdGenerator) {
        this.configProperties = configProperties;
        this.userIdGenerator = userIdGenerator;
    }


    public KeyPair generateKeyPair(){
        try {
            log.debug("Started Generating Key Pair ...");
            Generator<KeyPair> keyPairGenerator = new MultiThreadChallengedKeyGenerator(new ChallengedKeyGeneratorDecorator(new KeyGenerator(),getChallengeZeros(), userIdGenerator), configProperties.getKeyGenerationThreads());
            return keyPairGenerator.generate();
        }catch (Exception e){
            log.debug("Finished Generating Key Pair");
            throw e;
        }
    }

    private int getChallengeZeros(){
        switch (configProperties.getNodeType()) {
            case RING:
                return KeyGenerationConfig.RING_CHALLENGE_INT;
        }
        return 1;
    }
}
