package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.RingMemberEntity;

public interface RingMemberRepository {
    void save(RingMemberEntity ringMemberEntity);
    RingMemberEntity get();
}
