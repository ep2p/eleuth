package com.github.ep2p.eleuth.util;

import org.cryptacular.util.CertUtil;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class CertificateUtil {
    public static String getCN(Certificate certificate){
        return CertUtil.subjectCN((X509Certificate) certificate);
    }
}
