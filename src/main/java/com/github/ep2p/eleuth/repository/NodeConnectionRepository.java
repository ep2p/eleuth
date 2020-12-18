package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.entity.NodeConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface NodeConnectionRepository extends JpaRepository<NodeConnectionEntity, Integer> {
    List<NodeConnectionEntity> findAllByNodeTypeAndLastUpdateBefore(NodeType nodeType, Date date);
    boolean existsByNodeId(BigInteger nodeId);
    NodeConnectionEntity findByNodeId(BigInteger nodeId);
}