package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.CertificateUtil;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.node.Node;
import com.github.ep2p.kademlia.node.external.ExternalNode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CertificateCollectorNodeConnectionApiDecorator extends NodeConnectionApiDecorator<BigInteger, ROWConnectionInfo> {
    private final KeyStoreWrapper keyStoreWrapper;
    private final NodeInformation nodeInformation;

    public CertificateCollectorNodeConnectionApiDecorator(NodeConnectionApi<BigInteger, ROWConnectionInfo> nodeConnectionApi, KeyStoreWrapper keyStoreWrapper, NodeInformation nodeInformation) {
        super(nodeConnectionApi);
        this.keyStoreWrapper = keyStoreWrapper;
        this.nodeInformation = nodeInformation;
    }

    @Override
    public FindNodeAnswer<BigInteger, ROWConnectionInfo> findNode(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node, BigInteger destination) {
        FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = super.findNode(caller, node, destination);
        List<ExternalNode<BigInteger, ROWConnectionInfo>> untrusted = new ArrayList<>();
        findNodeAnswer.getNodes().forEach(externalNode -> {
            String certificate = externalNode.getConnectionInfo().getCertificate();
            try {
                Certificate encodedCertificate = keyStoreWrapper.getEncodedCertificate(certificate);
                String cn = CertificateUtil.getCN(encodedCertificate);
                if(cn.equals("main") || externalNode.getId().toString().equals("main") || externalNode.getId().equals(nodeInformation.getId())){
                    untrusted.add(externalNode);
                    return;
                }
                keyStoreWrapper.addCertificate(encodedCertificate, externalNode.getId().toString());
            } catch (CertificateException e) {
                untrusted.add(externalNode);
            } catch (Exception e) {
                log.error("Failed to add certificate", e);
            }
        });
        findNodeAnswer.getNodes().removeAll(untrusted);
        return findNodeAnswer;
    }
}
