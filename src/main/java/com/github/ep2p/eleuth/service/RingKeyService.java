package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.model.entity.RingMemberEntity;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.RingMemberRepository;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.key.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.ep2p.eleuth.config.KeyGenerationConfig.PARTIAL_RING_KEY_PART_SIZE;
import static com.github.ep2p.eleuth.config.KeyGenerationConfig.RING_CHALLENGE_INT;

@Slf4j
@Service
public class RingKeyService {
    private final RingKeyGenerator ringKeyGenerator;
    private volatile boolean requested = true;
    private final RingMemberRepository ringMemberRepository;
    private final NodeInformation nodeInformation;

    public RingKeyService(UserIdGenerator<BigInteger> userIdGenerator, RingMemberRepository ringMemberRepository, NodeInformation nodeInformation) {
        this.ringMemberRepository = ringMemberRepository;
        this.nodeInformation = nodeInformation;
        KeyGenerator keyGenerator = new KeyGenerator();
        ChallengedKeyGeneratorDecorator challengedKeyGeneratorDecorator = new ChallengedKeyGeneratorDecorator(keyGenerator, RING_CHALLENGE_INT, userIdGenerator);
        UserIdGenerator<String> userIdPartial128Generator = new PubHashUserIdPartial128Generator(PARTIAL_RING_KEY_PART_SIZE);
        this.ringKeyGenerator = new RingKeyGenerator(10, challengedKeyGeneratorDecorator, userIdPartial128Generator);
    }


    public void importMembership(RingMemberEntity ringMemberEntity){
        ringMemberEntity.setPartial(true);
        ringMemberRepository.save(ringMemberEntity);
        nodeInformation.setRingKey(ringMemberEntity.getKey());
    }

    public RingMemberEntity exportPart(int p){
        RingMemberEntity ringMemberEntity = exportFullMembership();
        return RingMemberEntity.builder()
                .keys(Collections.singletonList(ringMemberEntity.getKeys().get(p - 1)))
                .key(ringMemberEntity.getKey())
                .build();
    }

    public RingMemberEntity exportFullMembership(){
        return ringMemberRepository.get();
    }


    public void generate(){
        if(requested)
            return;

        if(ringMemberRepository.get() != null){
            return;
        }
        requested = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("Generating a new ring key");
                RingKeyGenerator.RingKey ringKey = ringKeyGenerator.generate();
                log.info("Generated ring key: " + ringKey.getKey());

                List<RingMemberEntity.PubPrv> list = new ArrayList<>();
                ringKey.getKeyPairs().forEach(keyPair -> {
                    int i1 = ringKey.getKeyPairs().indexOf(keyPair);
                    list.add(RingMemberEntity.PubPrv.builder()
                            .part(i1 + 1)
                            .partialKey(ringKey.getIds().get(i1))
                            .privateKey(Base64Util.encode(keyPair.getPrivate().getEncoded()))
                            .publicKey(Base64Util.encode(keyPair.getPublic().getEncoded()))
                            .build());
                });

                ringMemberRepository.save(RingMemberEntity.builder()
                        .key(ringKey.getKey())
                        .keys(list)
                        .build());
                nodeInformation.setRingKey(ringKey.getKey());
                log.info("Successfully saved ring keys");
            }
        }).start();

    }

}
