package com.github.ep2p.eleuth.config;

import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.exception.NodeIsOfflineException;
import com.github.ep2p.kademlia.node.KademliaNode;
import com.github.ep2p.kademlia.node.KademliaNodeListener;
import com.github.ep2p.kademlia.node.KademliaNodeListenerDecorator;
import com.github.ep2p.kademlia.node.Node;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class EleuthKademliaNodeListenerDecorator extends KademliaNodeListenerDecorator<BigInteger, ROWConnectionInfo, Key, String> {

    public EleuthKademliaNodeListenerDecorator(KademliaNodeListener<BigInteger, ROWConnectionInfo, Key, String> kademliaNodeListener) {
        super(kademliaNodeListener);
    }

    @Override
    public void onBootstrapDone(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode) {
        super.onBootstrapDone(kademliaNode);
        log.info("Finished bootstrapping");
        kademliaNode.getRoutingTable().getBuckets().forEach(bucket -> {
            log.debug("bucket id: " + bucket.getId() + " => " + bucket.getNodeIds());
        });
    }

    @Override
    public synchronized void onNewNodeAvailable(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode, Node<BigInteger, ROWConnectionInfo> node) {
        super.onNewNodeAvailable(kademliaNode, node);
        log.debug("New node available: " + node.getId());
    }

    @Override
    public void onPing(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode, Node<BigInteger, ROWConnectionInfo> node) throws NodeIsOfflineException {
        super.onPing(kademliaNode, node);
        log.debug("Received ping request from: " + node.getId());
    }
}
