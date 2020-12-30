package com.github.ep2p.eleuth.repository.memory;

import com.github.ep2p.eleuth.model.entity.memory.NodeSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeSessionRepository extends JpaRepository<NodeSessionEntity, Integer> {
    List<NodeSessionEntity> findAllByNodeId(String nodeId);
    void deleteAllBySessionId(String sessionId);
    void deleteAllByUserId(String userId);
    void deleteAllByNodeId(String nodeId);
}
