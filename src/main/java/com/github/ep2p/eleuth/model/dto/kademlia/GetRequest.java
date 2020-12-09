package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@ToString
public class GetRequest extends BasicRequest {
    private NodeDto requester;
    private Key key;

    public GetRequest(Node<BigInteger, ROWConnectionInfo> requester) {
    }

    public GetRequest(SignedData<NodeDto> node, Node<BigInteger, ROWConnectionInfo> requester) {
        super(node);
        setRequester(requester);
    }

    public void setRequester(Node<BigInteger, ROWConnectionInfo> requester) {
        this.requester = new NodeDto(requester.getId(), requester.getConnectionInfo(), NodeType.RING, new Date().getTime());
    }

    public GetRequest() {
    }
}
