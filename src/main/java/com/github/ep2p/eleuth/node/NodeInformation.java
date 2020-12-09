package com.github.ep2p.eleuth.node;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.security.KeyPair;

@Builder
@Setter
@Getter
public class NodeInformation {
    private ROWConnectionInfo connectionInfo;
    private BigInteger id;
    private KeyPair keyPair;
    private NodeType nodeType;
}
