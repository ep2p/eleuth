package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.RoutingTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoutingTableRepository extends JpaRepository<RoutingTableEntity, Integer> {
    boolean existsByUniqueIsTrue();
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update #{#entityName} rt set rt.bytes = :bytes where rt.unique = true")
    void updateBytes(byte[] bytes);
}
