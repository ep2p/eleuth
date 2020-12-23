package com.github.ep2p.eleuth.repository.sloth;

import com.github.ep2p.eleuth.model.entity.RingMemberEntity;
import com.github.ep2p.eleuth.repository.RingMemberRepository;
import lab.idioglossia.jsonsloth.JsonSlothManager;
import org.springframework.stereotype.Repository;

@Repository
public class SlothRingMemberRepository implements RingMemberRepository {
    private final JsonSlothManager jsonSlothManager;

    public SlothRingMemberRepository(JsonSlothManager jsonSlothManager) {
        this.jsonSlothManager = jsonSlothManager;
    }

    @Override
    public void save(RingMemberEntity ringMemberEntity) {
        if (jsonSlothManager.getKeys(RingMemberEntity.class).contains(ringMemberEntity.getId())) {
            jsonSlothManager.update(ringMemberEntity);
        }else {
            jsonSlothManager.save(ringMemberEntity);
        }
    }

    @Override
    public RingMemberEntity get() {
        return jsonSlothManager.get("membership", RingMemberEntity.class);
    }
}
