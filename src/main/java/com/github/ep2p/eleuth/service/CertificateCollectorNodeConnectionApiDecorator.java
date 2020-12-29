package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.CertificateUtil;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.UserIdGenerator;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.node.Node;
import com.github.ep2p.kademlia.node.external.ExternalNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CertificateCollectorNodeConnectionApiDecorator extends NodeConnectionApiDecorator<BigInteger, ROWConnectionInfo> {
    private final KeyStoreWrapper keyStoreWrapper;
    private final UserIdGenerator<BigInteger> userIdGenerator;

    public CertificateCollectorNodeConnectionApiDecorator(NodeConnectionApi<BigInteger, ROWConnectionInfo> nodeConnectionApi, KeyStoreWrapper keyStoreWrapper, UserIdGenerator<BigInteger> userIdGenerator) {
        super(nodeConnectionApi);
        this.keyStoreWrapper = keyStoreWrapper;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public FindNodeAnswer<BigInteger, ROWConnectionInfo> findNode(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node, BigInteger destination) {
        FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = super.findNode(caller, node, destination);
        List<ExternalNode<BigInteger, ROWConnectionInfo>> untrusted = new ArrayList<>();
        findNodeAnswer.getNodes().forEach(externalNode -> {
            try {
                isValidNodeAndCert(externalNode);
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
            isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.storeAsync(caller, requester, node, key, value);
    }

    @Override
    public <K> void getRequest(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key) {
        try {
            isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.getRequest(caller, requester, node, key);
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, V value) {
        try {
            isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.sendGetResults(caller, requester, key, value);
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, boolean success) {
        try {
            isValidNodeAndCert(requester);
        } catch (Exception e) {
            log.error("Failed to validate requester node", e);
            return;
        }
        super.sendStoreResults(caller, requester, key, success);
    }

    private boolean isValidNodeAndCert(Node<BigInteger, ROWConnectionInfo> node) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        Certificate encodedCertificate = keyStoreWrapper.getEncodedCertificate(node.getConnectionInfo().getCertificate());
        boolean b = validateCertificate(encodedCertificate) && certificateMatchesId(encodedCertificate, node.getId());
        if(b){
            keyStoreWrapper.addCertificate(encodedCertificate, node.getId().toString());
        }
        return b;
    }

    private boolean certificateMatchesId(Certificate encodedCertificate, BigInteger nodeId){
        BigInteger generatedNodeId = userIdGenerator.generate(encodedCertificate.getPublicKey());
        return generatedNodeId.equals(nodeId);
    }

    private boolean validateCertificate(Certificate encodedCertificate) throws CertificateException {
        String cn = CertificateUtil.getCN(encodedCertificate);
        if(cn.equals("main")){
            return false;
        }
        return true;
    }
}
