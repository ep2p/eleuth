package com.github.ep2p.eleuth.model.dto.kademlia;

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
    private BigInteger id;
    private ROWConnectionInfo connection;
}
