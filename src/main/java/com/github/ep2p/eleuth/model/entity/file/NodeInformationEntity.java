package com.github.ep2p.eleuth.model.entity.file;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "node_informations", indexes = {
        @Index(columnList = "nodeId")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeInformationEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Date creationDate;
    private BigInteger nodeId;
    private byte[] data;
    @Builder.Default
    private int version = 1;
}
