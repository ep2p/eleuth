package com.github.ep2p.eleuth.util;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.Node;

import java.math.BigInteger;
import java.util.Date;

public class NodeUtil {
    public static Node<BigInteger, ROWConnectionInfo> getNodeFromDto(NodeDto nodeDto){
        return new Node<BigInteger, ROWConnectionInfo>(nodeDto.getId(), nodeDto.getConnectionInfo(), new Date());
    }
}
