package com.github.ep2p.eleuth.service.row;

import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rowNodeConnectionApi")
@Slf4j
public class ROWNodeConnectionApi implements NodeConnectionApi<ROWConnectionInfo> {
    private final RowConnectionPool rowConnectionPool;

    @Autowired
    public ROWNodeConnectionApi(RowConnectionPool rowConnectionPool) {
        this.rowConnectionPool = rowConnectionPool;
    }

    @Override
    public PingAnswer ping(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> node) {
        return null;
    }

    @Override
    public void shutdownSignal(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> node) {
        return;
    }

    @Override
    public FindNodeAnswer<ROWConnectionInfo> findNode(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> node, Integer nodeId) {
        return null;
    }

    //not async yet
    @Override
    public <K, V> void storeAsync(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> requester, Node<ROWConnectionInfo> node, K key, V value) {
        return;
    }

    @Override
    public <K> void getRequest(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> requester, Node<ROWConnectionInfo> node, K key) {
        return;
    }

    @Override
    public <K, V> void sendGetResults(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> requester, K key, V value) {
        return;
    }

    @Override
    public <K> void sendStoreResults(Node<ROWConnectionInfo> caller, Node<ROWConnectionInfo> requester, K key, boolean success) {
        return;
    }

    private Integer getKey(Object k){
        assert k instanceof Integer;
        return (Integer) k;
    }

    private String getValue(Object v){
        assert v instanceof String;
        return (String) v;
    }
}
