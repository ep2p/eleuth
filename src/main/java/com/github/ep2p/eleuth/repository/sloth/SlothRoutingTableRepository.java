package com.github.ep2p.eleuth.repository.sloth;

import com.github.ep2p.eleuth.model.entity.RoutingTableEntity;
import com.github.ep2p.eleuth.repository.RoutingTableRepository;
import lab.idioglossia.sloth.collection.Collection;
import lab.idioglossia.sloth.collection.Value;
import lab.idioglossia.sloth.storage.SlothStorage;
import org.springframework.stereotype.Repository;

@Repository
public class SlothRoutingTableRepository implements RoutingTableRepository {
    private final Collection<Object, byte[]> kademliaCollection;
    private final String KEY = "routingTable";

    public SlothRoutingTableRepository(SlothStorage slothStorage) {
        this.kademliaCollection = slothStorage.getCollectionOfType("kademlia", Collection.Type.MAP, byte[].class, ".data");
    }

    @Override
    public boolean existsByUniqueIsTrue() {
        return this.kademliaCollection.getKeys().contains(KEY);
    }

    @Override
    public void updateBytes(byte[] bytes) {
        this.kademliaCollection.update(KEY, new Value<byte[]>() {
            @Override
            public byte[] getData() {
                return bytes;
            }
        });
    }

    @Override
    public RoutingTableEntity save(RoutingTableEntity routingTableEntity) {
        if(existsByUniqueIsTrue()){
            updateBytes(routingTableEntity.getBytes());
        }else {
            this.kademliaCollection.save(KEY, new Value<byte[]>() {
                @Override
                public byte[] getData() {
                    return routingTableEntity.getBytes();
                }
            });
        }
        return routingTableEntity;
    }

    @Override
    public RoutingTableEntity findByUniqueIsTrue() {
        Value<byte[]> value = this.kademliaCollection.get(KEY);
        return RoutingTableEntity.builder().bytes(value.getData()).unique(true).build();
    }
}
