package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.entity.Key;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResultRequest extends BasicRequest {
    private Key key;
    private boolean success;

    public StoreResultRequest(SignedData<NodeDto> node) {
        super(node);
    }

    public StoreResultRequest() {
    }
}
