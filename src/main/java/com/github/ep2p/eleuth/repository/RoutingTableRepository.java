package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;

public interface RoutingTableRepository {
    boolean exists();
    void store(BigIntegerRoutingTable<ROWConnectionInfo> routingTable);
    BigIntegerRoutingTable<ROWConnectionInfo> get();
}
