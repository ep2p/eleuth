package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDto implements Serializable {
    private static final long serialVersionUID = -3883150909228695481L;
    private BigInteger id;
    private ROWConnectionInfo connectionInfo;
    private NodeType type;
    private long timestamp;
}
