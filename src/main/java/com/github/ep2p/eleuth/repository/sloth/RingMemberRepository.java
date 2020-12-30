package com.github.ep2p.eleuth.repository.sloth;

import com.github.ep2p.eleuth.model.entity.file.RingMemberEntity;

public interface RingMemberRepository {
    void save(RingMemberEntity ringMemberEntity);
    RingMemberEntity get();
}
