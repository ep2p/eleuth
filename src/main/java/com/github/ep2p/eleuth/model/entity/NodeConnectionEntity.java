package com.github.ep2p.eleuth.model.entity;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "node_connection", indexes = {
        @Index(columnList = "nodeId", unique = true),
        @Index(columnList = "ringId")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeConnectionEntity extends ROWConnectionInfo {
    private static final long serialVersionUID = 6585427476789728619L;
    @Id
    @GeneratedValue
    private Integer id;
    private BigInteger nodeId;
    private Date lastUpdate;
    private NodeType nodeType;
    private String sessionId;
    private String userId;
    private BigInteger ringId;
}
