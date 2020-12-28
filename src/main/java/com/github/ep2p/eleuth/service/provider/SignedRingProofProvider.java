package com.github.ep2p.eleuth.service.provider;

import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.entity.RingMemberEntity;
import com.github.ep2p.eleuth.repository.RingMemberRepository;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SignedRingProofProvider {
    private final RingMemberRepository ringMemberRepository;
    private final MessageSignatureService messageSignatureService;

    @Autowired
    public SignedRingProofProvider(RingMemberRepository ringMemberRepository, MessageSignatureService messageSignatureService) {
        this.ringMemberRepository = ringMemberRepository;
        this.messageSignatureService = messageSignatureService;
    }

    @Cacheable(cacheManager = "signedNodeInformationCache", value = "membership")
    public SignedData<RingMemberProofDto> getRingProof(){
        RingMemberEntity ringMemberEntity = ringMemberRepository.get();
        RingMemberEntity.PubPrv pubPrv = ringMemberEntity.getKeys().get(0);
        RingMemberProofDto ringMemberProofDto = RingMemberProofDto.builder().key(ringMemberEntity.getKey())
                .part(ringMemberEntity.isPartial() ? pubPrv.getPart() : 1)
                .build();

        return messageSignatureService.sign(ringMemberProofDto, pubPrv.getPrivateKey(), pubPrv.getPublicKey());
    }
}
