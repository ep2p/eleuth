package com.github.ep2p.eleuth.model.dto;

import com.github.ep2p.eleuth.model.dto.kademlia.BasicRequest;
import com.github.ep2p.eleuth.model.dto.kademlia.NodeDto;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

@Getter
@Setter
@ToString
public class GetRequest extends BasicRequest {
    private NodeDto requester;
    private Integer key;

    public GetRequest(Node<BigInteger, ROWConnectionInfo> requester) {
    }

    public GetRequest(SignedData<NodeDto> node, Node<BigInteger, ROWConnectionInfo> requester) {
        super(node);
        setRequester(requester);
    }

    public void setRequester(Node<BigInteger, ROWConnectionInfo> requester) {
        this.requester = new NodeDto(requester.getId(), requester.getConnectionInfo());
    }

    public GetRequest() {
    }
}
