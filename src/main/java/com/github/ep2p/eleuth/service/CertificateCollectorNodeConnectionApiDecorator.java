package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.NodeAndCertificateValidatorUtil;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.UserIdGenerator;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.node.Node;
import com.github.ep2p.kademlia.node.external.ExternalNode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CertificateCollectorNodeConnectionApiDecorator extends NodeConnectionApiDecorator<BigInteger, ROWConnectionInfo> {
    private final NodeAndCertificateValidatorUtil nodeAndCertificateValidatorUtil;

    public CertificateCollectorNodeConnectionApiDecorator(NodeConnectionApi<BigInteger, ROWConnectionInfo> nodeConnectionApi, KeyStoreWrapper keyStoreWrapper, UserIdGenerator<BigInteger> userIdGenerator) {
        super(nodeConnectionApi);
        nodeAndCertificateValidatorUtil = new NodeAndCertificateValidatorUtil(userIdGenerator, keyStoreWrapper);
    }

    @Override
    public FindNodeAnswer<BigInteger, ROWConnectionInfo> findNode(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node, BigInteger destination) {
        FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = super.findNode(caller, node, destination);
        List<ExternalNode<BigInteger, ROWConnectionInfo>> untrusted = new ArrayList<>();
        findNodeAnswer.getNodes().forEach(externalNode -> {
            try {
                nodeAndCertificateValidatorUtil.isValidNodeAndCert(externalNode);
            } catch (CertificateException e) {
                untrusted.add(externalNode);
            } catch (Exception e) {
                log.error("Failed to add certificate", e);
            }
        });
        findNodeAnswer.getNodes().removeAll(untrusted);
        return findNodeAnswer;
    }

    @Override
    public <K, V> void storeAsync(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key, V value) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.storeAsync(caller, requester, node, key, value);
    }

    @Override
    public <K> void getRequest(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.getRequest(caller, requester, node, key);
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, V value) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.sendGetResults(caller, requester, key, value);
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, boolean success) {
        try {
            nodeAndCertificateValidatorUtil.isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.sendStoreResults(caller, requester, key, success);
    }


}
