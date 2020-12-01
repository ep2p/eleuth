package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResultRequest extends BasicResponse {
    private Integer key;
    private boolean success;

    public StoreResultRequest(SignedData<NodeDto> node) {
        super(node);
    }

    public StoreResultRequest() {
    }
}
