package com.github.ep2p.eleuth.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.model.entity.NodeInfoStoreValue;
import com.github.ep2p.eleuth.model.entity.NodeInformationEntity;
import com.github.ep2p.encore.helper.Serializer;
import com.github.ep2p.kademlia.node.KademliaRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Repository("kademliaRepository")
@Slf4j
public class EleuthKademliaRepository implements KademliaRepository<Key, String> {
    private final Serializer<String> serializer = new Serializer<>();
    private final NodeInformationRepository nodeInformationRepository;
    private final ResultConcat resultConcat;

    public EleuthKademliaRepository(NodeInformationRepository nodeInformationRepository, ObjectMapper objectMapper) {
        this.nodeInformationRepository = nodeInformationRepository;
        resultConcat = new ResultConcat(objectMapper);
    }

    @SneakyThrows
    @Override
    public void store(Key key, String value) {
        byte[] serialized = serializer.serialize(value);
        if(key.getType().equals(Key.Type.NODE_INFO)){
            nodeInformationRepository.save(NodeInformationEntity.builder()
                    .nodeId(new BigInteger(key.getId()))
                    .creationDate(new Date())
                    .data(serialized)
                    .build());
        }
    }

    @Override
    @Cacheable(cacheManager = "repositoryCache", value = "repository", key = "#key.hashCode()")
    public String get(Key key) {
        if(key.getType().equals(Key.Type.NODE_INFO)){
            List<NodeInformationEntity> nodeInformationEntities =  nodeInformationRepository.findAllByNodeId(new BigInteger(key.getId()));
            return resultConcat.concatNodeInformationData(nodeInformationEntities);
        }
        return "";
    }

    @Override
    public void remove(Key key) {
        if(key.getType().equals(Key.Type.NODE_INFO)){
            nodeInformationRepository.deleteAllByNodeId(new BigInteger(key.getId()));
        }

    }

    @Override
    public boolean contains(Key key) {
        if(key.getType().equals(Key.Type.NODE_INFO)){
            return nodeInformationRepository.existsByNodeId(new BigInteger(key.getId()));
        }
        return false;
    }


    //todo: important decision
    @Override
    public List<Key> getKeys() {
        return new ArrayList<>();
    }

    private class ResultConcat {
        private final ObjectMapper objectMapper;

        private ResultConcat(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @SneakyThrows
        public String concatNodeInformationData(List<NodeInformationEntity> nodeInformationEntities){
            NodeInfoStoreValue nodeInfoStoreValue = new NodeInfoStoreValue();
            ArrayList<SignedData<NodeDto>> routes = new ArrayList<>();

            Set<BigInteger> ids = new LinkedHashSet<>();
            nodeInformationEntities.forEach(nodeInformationEntity -> {
                try {
                    String deserialize = serializer.deserialize(nodeInformationEntity.getData());
                    NodeInfoStoreValue nodeInfoStoreValue1 = objectMapper.readValue(deserialize, NodeInfoStoreValue.class);
                    nodeInfoStoreValue1.getRoutes().forEach(nodeDtoSignedData -> {
                        if(!ids.contains(nodeDtoSignedData.getData().getId())){
                            routes.add(nodeDtoSignedData);
                            ids.add(nodeDtoSignedData.getData().getId());
                        }
                    });
                } catch (Exception e) {
                    log.error("Could not deserialize nodeInformationEntity data", e);
                }
            });

            nodeInfoStoreValue.setRoutes(routes);
            return objectMapper.writeValueAsString(nodeInfoStoreValue);
        }

    }
}
