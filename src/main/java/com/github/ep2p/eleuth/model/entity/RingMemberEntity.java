package com.github.ep2p.eleuth.model.entity;

import lab.idioglossia.jsonsloth.JsonSlothEntity;
import lab.idioglossia.jsonsloth.JsonSlothId;
import lab.idioglossia.sloth.collection.Collection;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonSlothEntity(type = Collection.Type.MAP, collectionName = "ring")
@Builder
@Data
public class RingMemberEntity {
    @JsonSlothId
    private final String id = "membership";
    private String key;
    private List<PubPrv> keys;
    @Builder.Default
    private boolean partial = false;


    @Builder
    @Data
    public static class PubPrv {
        private int part;
        private String partialKey;
        private String publicKey;
        private String privateKey;
    }
}
