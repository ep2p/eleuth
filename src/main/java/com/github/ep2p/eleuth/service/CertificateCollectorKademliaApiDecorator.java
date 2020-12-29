package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.model.dto.kademlia.*;
import com.github.ep2p.eleuth.util.NodeAndCertificateValidatorUtil;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.UserIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

import static com.github.ep2p.eleuth.util.NodeUtil.getNodeFromDto;

@Slf4j
public class CertificateCollectorKademliaApiDecorator extends KademliaApiDecorator {
    private final NodeAndCertificateValidatorUtil nodeAndCertificateValidatorUtil;

    public CertificateCollectorKademliaApiDecorator(KademliaApi kademliaApi, UserIdGenerator<BigInteger> userIdGenerator, KeyStoreWrapper keyStoreWrapper) {
        super(kademliaApi);
        nodeAndCertificateValidatorUtil = new NodeAndCertificateValidatorUtil(userIdGenerator, keyStoreWrapper);
    }

    @Override
    public PingResponse onPing(BasicRequest basicRequest) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(getNodeFromDto(basicRequest.getCaller().getData()));
            return super.onPing(basicRequest);
        } catch (Exception e) {
            log.error("Failed to validte and insert certificate of node", e);
            return new PingResponse();
        }
    }

    @Override
    public BasicResponse store(StoreRequest storeRequest) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(getNodeFromDto(storeRequest.getRequester()));
            return super.store(storeRequest);
        } catch (Exception e) {
            log.error("Failed to validte and insert certificate of node", e);
            return new BasicResponse();
        }
    }

    @Override
    public BasicResponse get(GetRequest getRequest) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(getNodeFromDto(getRequest.getRequester()));
            return super.get(getRequest);
        } catch (Exception e) {
            log.error("Failed to validte and insert certificate of node", e);
            return new BasicResponse();
        }
    }

    @Override
    public BasicResponse onGetResult(GetResultRequest getResultRequest) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(getNodeFromDto(getResultRequest.getCaller().getData()));
            return super.onGetResult(getResultRequest);
        } catch (Exception e) {
            log.error("Failed to validte and insert certificate of node", e);
            return new BasicResponse();
        }
    }

    @Override
    public BasicResponse onStoreResult(StoreResultRequest storeResultRequest) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(getNodeFromDto(storeResultRequest.getCaller().getData()));
            return super.onStoreResult(storeResultRequest);
        } catch (Exception e) {
            log.error("Failed to validte and insert certificate of node", e);
            return new BasicResponse();
        }
    }
}
