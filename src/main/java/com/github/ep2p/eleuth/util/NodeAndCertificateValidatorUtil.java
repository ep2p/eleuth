package com.github.ep2p.eleuth.util;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.encore.key.UserIdGenerator;
import com.github.ep2p.kademlia.node.Node;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class NodeAndCertificateValidatorUtil {
    private final UserIdGenerator<BigInteger> userIdGenerator;
    private final KeyStoreWrapper keyStoreWrapper;

    public NodeAndCertificateValidatorUtil(UserIdGenerator<BigInteger> userIdGenerator, KeyStoreWrapper keyStoreWrapper) {
        this.userIdGenerator = userIdGenerator;
        this.keyStoreWrapper = keyStoreWrapper;
    }

    public boolean isValidNodeAndCert(Node<BigInteger, ROWConnectionInfo> node) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        Certificate encodedCertificate = keyStoreWrapper.getEncodedCertificate(node.getConnectionInfo().getCertificate());
        boolean b = validateCertificate(encodedCertificate) && certificateMatchesId(encodedCertificate, node.getId());
        if(b){
            keyStoreWrapper.addCertificate(encodedCertificate, node.getId().toString());
        }
        return b;
    }

    public boolean certificateMatchesId(Certificate encodedCertificate, BigInteger nodeId){
        BigInteger generatedNodeId = userIdGenerator.generate(encodedCertificate.getPublicKey());
        return generatedNodeId.equals(nodeId);
    }

    public boolean validateCertificate(Certificate encodedCertificate) throws CertificateException {
        String cn = CertificateUtil.getCN(encodedCertificate);
        if(cn.equals("main")){
            return false;
        }
        return true;
    }

}
