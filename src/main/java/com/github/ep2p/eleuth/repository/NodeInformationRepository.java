package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.NodeInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface NodeInformationRepository extends JpaRepository<NodeInformationEntity, Long> {
    List<NodeInformationEntity> findAllByNodeId(BigInteger nodeId);
    void deleteAllByNodeId(BigInteger nodeId);
    boolean existsByNodeId(BigInteger nodeId);
}
