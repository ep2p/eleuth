package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class ProtocolResponse<E extends Serializable> extends BaseResponse implements Serializable {
    private E reply;
}
