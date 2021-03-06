package com.github.ep2p.eleuth.service.stage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.model.entity.NodeInfoStoreValue;
import com.github.ep2p.eleuth.service.EleuthKademliaRepositoryNode;
import com.github.ep2p.eleuth.service.provider.SignedNodeDtoProvider;
import com.github.ep2p.eleuth.util.Pipeline;
import com.github.ep2p.kademlia.exception.StoreException;
import com.github.ep2p.kademlia.model.StoreAnswer;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//Only on RING nodes, calls store method on kademlia node to store Availability message
@Slf4j
public class AvailabilityStoreStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final EleuthKademliaRepositoryNode kademliaNode;
    private final SignedNodeDtoProvider signedNodeDtoProvider;
    private final ObjectMapper objectMapper;

    public AvailabilityStoreStage(EleuthKademliaRepositoryNode kademliaNode, SignedNodeDtoProvider signedNodeDtoProvider, ObjectMapper objectMapper) {
        this.kademliaNode = kademliaNode;
        this.signedNodeDtoProvider = signedNodeDtoProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        try {
            StoreAnswer<BigInteger, Key> storeAnswer = kademliaNode.store(Key.builder()
                            .type(Key.Type.NODE_INFO)
                            .id(availabilityMessage.getMessage().getBody().getData().getNodeId().toString())
                            .build(),
                            getValue(availabilityMessage),
                            1, TimeUnit.MINUTES
                    );
            if (!storeAnswer.getResult().equals(StoreAnswer.Result.STORED)) {
                throw new StoreException();
            }
        } catch (StoreException | JsonProcessingException | InterruptedException e) {
            o.setFailed(true);
            o.getErrorMessages().add("Failed to store data");
            log.error("Failed to store data", e);
            return false;
        }
        return true;
    }

    private String getValue(AvailabilityMessage availabilityMessage) throws JsonProcessingException {
        SignedData<NodeDto> route = availabilityMessage.getMessage().getRoute();
        SignedData<NodeDto> route2 = signedNodeDtoProvider.getWithCertificate();
        NodeInfoStoreValue nodeInfoStoreValue = new NodeInfoStoreValue(Arrays.asList(route, route2));
        return objectMapper.writeValueAsString(nodeInfoStoreValue);
    }
}
