package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.api.NodeInformationDto;
import com.github.ep2p.eleuth.model.dto.kademlia.NodeDto;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.Base64Util;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.helper.Serializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
@Slf4j
public class ApiService {
    private final KeyStoreWrapper keyStoreWrapper;
    private final NodeInformation nodeInformation;
    private final ROWConnectionInfo rowConnectionInfo;
    private final MessageSignatureService messageSignatureService;
    private NodeInformationDto nodeInformationDto;

    public ApiService(KeyStoreWrapper keyStoreWrapper, NodeInformation nodeInformation, ROWConnectionInfo rowConnectionInfo, MessageSignatureService messageSignatureService) {
        this.keyStoreWrapper = keyStoreWrapper;
        this.nodeInformation = nodeInformation;
        this.rowConnectionInfo = rowConnectionInfo;
        this.messageSignatureService = messageSignatureService;
    }


    @SneakyThrows
    @PostConstruct
    public void constructNodeInformationDto(){
        NodeInformationDto.NodeInfo nodeInfo = NodeInformationDto.NodeInfo.extendFrom(NodeDto.builder()
                .id(nodeInformation.getId())
                .connectionInfo(rowConnectionInfo)
                .build());
        Certificate main = keyStoreWrapper.getCertificate("main");
        String encode = Base64Util.encode(main.getEncoded());
        nodeInfo.setCertificate(encode);
        SignedData<NodeInformationDto.NodeInfo> sign = messageSignatureService.sign(nodeInfo, true);
        this.nodeInformationDto = new NodeInformationDto();
        this.nodeInformationDto.setInfo(sign);
    }

    public NodeInformationDto getNodeInformationDto(){
        return this.nodeInformationDto;
    }

    @SneakyThrows
    public BaseResponse addNodeInformation(NodeInformationDto nodeInformationDto){
        try {
            messageSignatureService.validate(nodeInformationDto.getInfo());
            PublicKey publicKey = messageSignatureService.getPublicKey(nodeInformationDto.getInfo());
            if(nodeInformationDto.getInfo().getData().getCertificate() != null){
                Certificate certificate = keyStoreWrapper.getEncodedCertificate(nodeInformationDto.getInfo().getData().getCertificate());
                if (!certificate.getPublicKey().equals(publicKey)) {
                    throw new InvalidSignatureException("Certificate public key and provided public key dont match");
                }
                keyStoreWrapper.addCertificate(certificate, nodeInformationDto.getInfo().getData().getId().toString());
            }
            return new BaseResponse(BaseResponse.Status.SUCCESS);
        } catch (InvalidSignatureException | CertificateException e) {
            log.error("Failed to add node information", e);
            return new BaseResponse(BaseResponse.Status.FAIL);
        }
    }
}
