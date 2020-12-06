package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.encore.helper.Serializer;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import lab.idioglossia.sloth.Collection;
import lab.idioglossia.sloth.SlothStorage;
import lab.idioglossia.sloth.Value;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

@Repository
public class SlothRoutingTableRepository implements RoutingTableRepository {
    private final Collection<String, byte[]> collection;
    private final String key = "routing_table";
    private final Serializer<BigIntegerRoutingTable<ROWConnectionInfo>> serializer = new Serializer<>();

    public SlothRoutingTableRepository(SlothStorage slothStorage) {
        this.collection = slothStorage.getCollectionOfType("data", Collection.Type.MAP, byte[].class, ".data");
    }

    @Override
    public boolean exists() {
        return collection.getKeys().contains(key);
    }

    @SneakyThrows
    @Override
    public void store(BigIntegerRoutingTable<ROWConnectionInfo> routingTable) {
        byte[] serializedData = serializer.serialize(routingTable);
        collection.save(key, new Value<byte[]>() {
            @Override
            public byte[] getData() {
                return serializedData;
            }
        });
    }

    @SneakyThrows
    @Override
    public BigIntegerRoutingTable<ROWConnectionInfo> get() {
        return serializer.deserialize(collection.get(key).getData());
    }
}
