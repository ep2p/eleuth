package com.github.ep2p.eleuth.config.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ep2p.kademlia.node.external.ExternalNode;

public class ExternalNodeModule extends SimpleModule {
    public ExternalNodeModule() {
        super();
        addDeserializer(ExternalNode.class, new ExternalNodeDeSerializer());
        addSerializer(ExternalNode.class, new ExternalNodeSerializer());
    }
}
