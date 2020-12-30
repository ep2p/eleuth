package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.file.RoutingTableEntity;
import com.github.ep2p.eleuth.repository.sloth.RoutingTableRepository;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.encore.helper.Serializer;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

@Repository
public class RoutingTableManager {
    private final Serializer<BigIntegerRoutingTable<ROWConnectionInfo>> serializer = new Serializer<>();
    private final RoutingTableRepository routingTableRepository;

    public RoutingTableManager(RoutingTableRepository routingTableRepository) {
        this.routingTableRepository = routingTableRepository;
    }

    @SneakyThrows
    public synchronized void store(BigIntegerRoutingTable<ROWConnectionInfo> routingTable) {
        byte[] serializedData = serializer.serialize(routingTable);
        if (routingTableRepository.existsByUniqueIsTrue()) {
            routingTableRepository.updateBytes(serializedData);
        }else {
            routingTableRepository.save(RoutingTableEntity.builder().bytes(serializedData).build());
        }
    }

    @SneakyThrows
    public synchronized BigIntegerRoutingTable<ROWConnectionInfo> get() {
        if(routingTableRepository.existsByUniqueIsTrue()){
            RoutingTableEntity routingTableEntity = routingTableRepository.findByUniqueIsTrue();
            return serializer.deserialize(routingTableEntity.getBytes());
        }
        return null;
    }
}
