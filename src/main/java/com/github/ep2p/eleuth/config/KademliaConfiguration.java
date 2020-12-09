package com.github.ep2p.eleuth.config;

import com.github.ep2p.eleuth.config.annotation.ConditionalOnRing;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.EleuthKademliaRepository;
import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.repository.RoutingTableLoader;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.ROWNodeConnectionApi;
import com.github.ep2p.kademlia.node.KademliaNode;
import com.github.ep2p.kademlia.node.KademliaRepository;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.node.RedistributionKademliaNodeListener;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigInteger;

@Configuration
@ConditionalOnRing
@Slf4j
public class KademliaConfiguration {
    private final RoutingTableLoader routingTableLoader;
    private KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode;

    public KademliaConfiguration(RoutingTableLoader routingTableLoader) {
        this.routingTableLoader = routingTableLoader;
    }

    @Bean
    public KademliaRepository<Key,String> kademliaRepository(){
        return new EleuthKademliaRepository();
    }


    @Bean("routingTable")
    @DependsOn("nodeInformation")
    public BigIntegerRoutingTable<ROWConnectionInfo> routingTable(NodeInformation nodeInformation){
        BigIntegerRoutingTable<ROWConnectionInfo> bigIntegerRoutingTable = routingTableLoader.get();
        if(bigIntegerRoutingTable != null)
            return bigIntegerRoutingTable;

        return new BigIntegerRoutingTable<ROWConnectionInfo>(nodeInformation.getId());
    }

    @Bean("kademliaNode")
    @DependsOn({"rowNodeConnectionApi", "kademliaRepository", "rowConnectionInfo", "nodeInformation", "routingTable"})
    public KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode(ROWNodeConnectionApi rowNodeConnectionApi, ROWConnectionInfo rowConnectionInfo, KademliaRepository<Key, String> kademliaRepository, NodeInformation nodeInformation, BigIntegerRoutingTable<ROWConnectionInfo> routingTable) throws IOException {
        KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> node = new KademliaSyncRepositoryNode<>(nodeInformation.getId(), routingTable, rowNodeConnectionApi, rowConnectionInfo, kademliaRepository);
        node.setKademliaNodeListener(new EleuthKademliaNodeListenerDecorator(new RedistributionKademliaNodeListener<BigInteger, ROWConnectionInfo, Key, String>(true, new RedistributionKademliaNodeListener.ShutdownDistributionListener<BigInteger, ROWConnectionInfo>() {
            @Override
            public void onFinish(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode) {

            }
        })));
        node.start();
        rowNodeConnectionApi.init(node);
        this.kademliaNode = node;
        return node;
    }

    @PreDestroy
    public void onDestroy(){
        if(this.kademliaNode != null){
            BigIntegerRoutingTable<ROWConnectionInfo> routingTable = (BigIntegerRoutingTable<ROWConnectionInfo>) this.kademliaNode.getRoutingTable();
            routingTableLoader.store(routingTable);
        }
    }

}
