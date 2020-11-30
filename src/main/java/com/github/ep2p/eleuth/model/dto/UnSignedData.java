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
public class UnSignedData<E extends Serializable> implements Serializable {
    private E data;
    private final boolean signed = false;
}
