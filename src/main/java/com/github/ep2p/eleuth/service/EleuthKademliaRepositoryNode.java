package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.exception.StoreException;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.StoreAnswer;
import com.github.ep2p.kademlia.node.KademliaRepository;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.table.Bucket;
import com.github.ep2p.kademlia.table.RoutingTable;

import java.math.BigInteger;

public class EleuthKademliaRepositoryNode extends KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> {

    public EleuthKademliaRepositoryNode(BigInteger nodeId, RoutingTable<BigInteger, ROWConnectionInfo, Bucket<BigInteger, ROWConnectionInfo>> routingTable, NodeConnectionApi<BigInteger, ROWConnectionInfo> nodeConnectionApi, ROWConnectionInfo connectionInfo, KademliaRepository<Key, String> kademliaRepository) {
        super(nodeId, routingTable, nodeConnectionApi, connectionInfo, kademliaRepository);
    }


    //the difference here is that we are storing NODE_INFO messages no matter what, and then passing it to other nodes if current node is not the closest node to key
    @Override
    public StoreAnswer<BigInteger, Key> store(Key key, String value) throws StoreException {
        if(!isRunning())
            throw new StoreException("Node is shutting down");
        StoreAnswer<BigInteger, Key> storeAnswer = null;
        BigInteger hash = hash(key);

        boolean stored = false;
        if(key.getType().equals(Key.Type.NODE_INFO)){
            getKademliaRepository().store(key, value);
            stored = true;
        }

        if(getId().equals(hash)) {
            if(!stored){
                getKademliaRepository().store(key, value);
            }
            storeAnswer = getNewStoreAnswer(key, StoreAnswer.Result.STORED, this);
        }else {
            FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = getRoutingTable().findClosest(hash);
            storeAnswer = findClosestNodesToStoreData(this, findNodeAnswer.getNodes(), key, value, null);
        }

        if(storeAnswer == null){
            throw new StoreException();
        }

        return storeAnswer;
    }
}
