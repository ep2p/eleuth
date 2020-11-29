package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.node.external.ExternalNode;

import java.io.IOException;
import java.math.BigInteger;

public class ExternalNodeSerializer extends JsonSerializer<ExternalNode> {

    @Override
    public void serialize(ExternalNode externalNode, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        ExternalNode<BigInteger, ROWConnectionInfo> externalNode1 = (ExternalNode<BigInteger, ROWConnectionInfo>) externalNode;
        jgen.writeStartObject();
        jgen.writeNumberField("id", externalNode1.getId());
        jgen.writeNumberField("distance", externalNode1.getDistance());
        jgen.writeObjectField("connectionInfo", externalNode.getConnectionInfo());
        jgen.writeEndObject();
    }
}
