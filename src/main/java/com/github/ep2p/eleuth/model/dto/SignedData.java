package com.github.ep2p.eleuth.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SignedData<E extends Serializable> implements Serializable {
    private static final long serialVersionUID = -5134312637473458058L;
    private E data;
    private String publicKey;
    private String signature;
    private String certificate;
    private final boolean signed = true;

    public SignedData(E data, String publicKey, String signature) {
        this.data = data;
        this.publicKey = publicKey;
        this.signature = signature;
    }

    public SignedData(E data, String publicKey, String signature, String certificate) {
        this.data = data;
        this.publicKey = publicKey;
        this.signature = signature;
        this.certificate = certificate;
    }
}
