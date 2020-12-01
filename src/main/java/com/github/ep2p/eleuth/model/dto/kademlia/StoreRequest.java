package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.Node;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class StoreRequest extends BasicRequest {
    private NodeDto requester;
    private Key key;
    private String value;

    public StoreRequest(Node<BigInteger, ROWConnectionInfo> requester) {
        setRequester(requester);
    }

    public StoreRequest(SignedData<NodeDto> caller, Node<BigInteger, ROWConnectionInfo> requester) {
        super(caller);
        setRequester(requester);
    }

    public void setRequester(Node<BigInteger, ROWConnectionInfo> requester) {
        this.requester = new NodeDto(requester.getId(), requester.getConnectionInfo());
    }

    public StoreRequest() {
    }
}
