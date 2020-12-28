package com.github.ep2p.eleuth.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "routing_table", indexes = {
        @Index(columnList = "unique")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutingTableEntity {
    @Id
    @GeneratedValue
    private Integer id;
    @Builder.Default
    private boolean unique = true;
    private byte[] bytes;
}
