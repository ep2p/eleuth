package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.RoutingTableEntity;

public interface RoutingTableRepository {
    boolean existsByUniqueIsTrue();
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("update #{#entityName} rt set rt.bytes = :bytes where rt.unique = true")
    void updateBytes(byte[] bytes);
    RoutingTableEntity save(RoutingTableEntity routingTableEntity);
    RoutingTableEntity findByUniqueIsTrue();
}
