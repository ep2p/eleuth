package com.github.ep2p.eleuth.model.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<E extends Serializable> implements Serializable {
    private Type type;
    private E message;

    public enum Type {

    }
}
