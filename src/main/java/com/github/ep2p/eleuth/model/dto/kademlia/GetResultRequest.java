package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetResultRequest extends BasicRequest {
    private Integer key;
    private String value;

    public GetResultRequest(SignedData<NodeDto> caller) {
        super(caller);
    }

    public GetResultRequest() {
    }
}
