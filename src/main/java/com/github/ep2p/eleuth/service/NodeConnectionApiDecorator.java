package com.github.ep2p.eleuth.service;

import com.github.ep2p.kademlia.connection.ConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.Node;

public abstract class NodeConnectionApiDecorator<ID extends Number, C extends ConnectionInfo> implements NodeConnectionApi<ID, C> {
    private final NodeConnectionApi<ID, C> nodeConnectionApi;

    public NodeConnectionApiDecorator(NodeConnectionApi<ID, C> nodeConnectionApi) {
        this.nodeConnectionApi = nodeConnectionApi;
    }

    @Override
    public PingAnswer<ID> ping(Node<ID, C> caller, Node<ID, C> node) {
        return nodeConnectionApi.ping(caller, node);
    }

    @Override
    public void shutdownSignal(Node<ID, C> caller, Node<ID, C> node) {
        nodeConnectionApi.shutdownSignal(caller, node);
    }

    @Override
    public FindNodeAnswer<ID, C> findNode(Node<ID, C> caller, Node<ID, C> node, ID destination) {
        return nodeConnectionApi.findNode(caller, node, destination);
    }

    @Override
    public <K, V> void storeAsync(Node<ID, C> caller, Node<ID, C> requester, Node<ID, C> node, K key, V value) {
        nodeConnectionApi.storeAsync(caller, requester, node, key, value);
    }

    @Override
    public <K> void getRequest(Node<ID, C> caller, Node<ID, C> requester, Node<ID, C> node, K key) {
        nodeConnectionApi.getRequest(caller, requester, node, key);
    }

    @Override
    public <K, V> void sendGetResults(Node<ID, C> caller, Node<ID, C> requester, K key, V value) {
        nodeConnectionApi.sendGetResults(caller, requester, key, value);
    }

    @Override
    public <K> void sendStoreResults(Node<ID, C> caller, Node<ID, C> requester, K key, boolean success) {
        nodeConnectionApi.sendStoreResults(caller, requester, key, success);
    }
}
