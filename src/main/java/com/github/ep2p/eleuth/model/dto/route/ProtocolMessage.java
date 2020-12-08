package com.github.ep2p.eleuth.model.dto.route;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
public abstract class ProtocolMessage<E extends Serializable> implements Serializable {
    private static final long serialVersionUID = 6261533941014022263L;
    private final Type type;
    private E message;

    public ProtocolMessage(Type type) {
        this.type = type;
    }

    public enum Type {
        AVAILABLE, DATA_MESSAGE, QUERY
    }
}
