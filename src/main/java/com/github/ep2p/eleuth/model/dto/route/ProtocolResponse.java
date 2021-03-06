package com.github.ep2p.eleuth.model.dto.route;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class ProtocolResponse<E extends Serializable> implements Serializable {
    private E reply;
    private final ProtocolMessage.Type type;

    protected ProtocolResponse(ProtocolMessage.Type type) {
        this.type = type;
    }
}
