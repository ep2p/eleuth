package com.github.ep2p.eleuth.config.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.node.external.BigIntegerExternalNode;
import com.github.ep2p.kademlia.node.external.ExternalNode;

import java.io.IOException;
import java.math.BigInteger;

public class ExternalNodeDeSerializer extends StdDeserializer<ExternalNode<BigInteger, ConnectionInfo>> {
    public ExternalNodeDeSerializer() {
        super(ExternalNode.class);
    }

    @Override
    public ExternalNode<BigInteger, ConnectionInfo> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        BigIntegerExternalNode<ConnectionInfo> bigIntegerExternalNode = new BigIntegerExternalNode<>();
        JsonNode jsonNode = jsonParser.readValueAsTree();
        if (jsonNode.has("distance")) {
            bigIntegerExternalNode.setDistance(new BigInteger(jsonNode.get("distance").asText()));
        }
        if(jsonNode.has("id")){
            bigIntegerExternalNode.setId(new BigInteger(jsonNode.get("id").asText()));
        }
        if(jsonNode.has("connectionInfo")){
            JsonNode jsonNode1 = jsonNode.get("connectionInfo");
            ROWConnectionInfo rowConnectionInfo = ROWConnectionInfo.builder()
                    .address(jsonNode1.get("address").asText())
                    .port(jsonNode1.get("port").asInt())
                    .ssl(jsonNode1.get("ssl").asBoolean())
                    .build();
            bigIntegerExternalNode.setConnectionInfo(rowConnectionInfo);
        }

        return bigIntegerExternalNode;
    }
}
