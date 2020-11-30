package com.github.ep2p.eleuth.service.row;

import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service("rowNodeConnectionApi")
@Slf4j
public class ROWNodeConnectionApi implements NodeConnectionApi<BigInteger, ROWConnectionInfo> {
    private final RowConnectionPool rowConnectionPool;

    @Autowired
    public ROWNodeConnectionApi(RowConnectionPool rowConnectionPool) {
        this.rowConnectionPool = rowConnectionPool;
    }

    @Override
    public PingAnswer<BigInteger> ping(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node) {
        return null;
    }

    @Override
    public void shutdownSignal(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node) {
        return;
    }

    @Override
    public FindNodeAnswer<BigInteger, ROWConnectionInfo> findNode(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node, BigInteger nodeId) {
        return null;
    }

    //not async yet
    @Override
    public <K, V> void storeAsync(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key, V value) {
        return;
    }

    @Override
    public <K> void getRequest(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key) {
        return;
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, V value) {
        return;
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, boolean success) {
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
