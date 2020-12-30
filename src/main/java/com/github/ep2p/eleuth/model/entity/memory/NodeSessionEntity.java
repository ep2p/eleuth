package com.github.ep2p.eleuth.model.entity.memory;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "nodeSession", indexes = {
        @Index(columnList = "nodeId,sessionId,userId", unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeSessionEntity {
    @Id
    @GeneratedValue
    private int id;
    private String sessionId;
    private String userId;
    private String nodeId;
}
