package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.node.NodeInformation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SignedNodeDtoProviderService {
    private final MessageSignatureService messageSignatureService;
    private final NodeInformation nodeInformation;


    public SignedNodeDtoProviderService(MessageSignatureService messageSignatureService, NodeInformation nodeInformation) {
        this.messageSignatureService = messageSignatureService;
        this.nodeInformation = nodeInformation;
    }

    @Cacheable(cacheManager = "signedNodeInformationCache", value = "publicKey")
    public SignedData<NodeDto> getWithPublicKey(){
        return messageSignatureService.sign(NodeDto.builder()
                .connectionInfo(nodeInformation.getConnectionInfo())
                .id(nodeInformation.getId())
                .type(NodeType.RING)
                .timestamp(new Date().getTime())
                .build(), true);
    }

    @Cacheable(cacheManager = "signedNodeInformationCache", value = "certificate")
    public SignedData<NodeDto> getWithCertificate(){
        return messageSignatureService.signWithCertificate(NodeDto.builder()
                .connectionInfo(nodeInformation.getConnectionInfo())
                .id(nodeInformation.getId())
                .type(NodeType.RING)
                .timestamp(new Date().getTime())
                .build());
    }

}
