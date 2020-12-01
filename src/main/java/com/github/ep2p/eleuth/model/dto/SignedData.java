package com.github.ep2p.eleuth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignedData<E extends Serializable> implements Serializable {
    private E data;
    private String publicKey;
    private String signature;
    private final boolean signed = true;
}
