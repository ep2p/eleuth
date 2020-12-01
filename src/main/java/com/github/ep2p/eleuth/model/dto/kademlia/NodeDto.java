package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
public class NodeDto implements Serializable {
    private ROWConnectionInfo connection;
    private BigInteger id;
}
