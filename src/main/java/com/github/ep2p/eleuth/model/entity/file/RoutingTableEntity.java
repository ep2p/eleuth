package com.github.ep2p.eleuth.model.entity.file;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
