package com.github.ep2p.eleuth.model.entity.file;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "node_connection", indexes = {
        @Index(columnList = "nodeId", unique = true),
        @Index(columnList = "ringKey")
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
    private String ringKey;
}
