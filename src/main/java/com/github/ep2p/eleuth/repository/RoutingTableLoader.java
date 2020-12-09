package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.RoutingTableEntity;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.encore.helper.Serializer;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public class RoutingTableLoader {
    private final Serializer<BigIntegerRoutingTable<ROWConnectionInfo>> serializer = new Serializer<>();
    private final RoutingTableRepository routingTableRepository;

    public RoutingTableLoader(RoutingTableRepository routingTableRepository) {
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
            Page<RoutingTableEntity> page = routingTableRepository.findAll(new OffsetLimitPageable(0, 1));
            return serializer.deserialize(page.getContent().get(0).getBytes());
        }
        return null;
    }
}
