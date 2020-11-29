package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.external.ExternalNode;

import java.io.IOException;
import java.math.BigInteger;

public class ExternalNodeSerializer extends JsonSerializer<ExternalNode<BigInteger, ROWConnectionInfo>> {

    @Override
    public void serialize(ExternalNode<BigInteger, ROWConnectionInfo> externalNode, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", externalNode.getId());
        jgen.writeNumberField("distance", externalNode.getDistance());
        jgen.writeObjectField("connectionInfo", externalNode.getConnectionInfo());
        jgen.writeEndObject();
    }
}
