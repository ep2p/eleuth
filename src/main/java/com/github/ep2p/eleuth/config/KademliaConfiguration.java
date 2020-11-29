package com.github.ep2p.eleuth.config;

import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.InMemoryKademliaRepository;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.ROWNodeConnectionApi;
import com.github.ep2p.kademlia.node.KademliaNode;
import com.github.ep2p.kademlia.node.KademliaRepository;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.node.RedistributionKademliaNodeListener;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.math.BigInteger;

@Configuration
@ConditionalOnProperty(prefix = "config", name = "nodeType", havingValue = "RING")
@EnableConfigurationProperties({NodeProperties.class})
public class KademliaConfiguration {
    private final NodeProperties nodeProperties;

    public KademliaConfiguration(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    @Bean
    public KademliaRepository<Integer,String> kademliaRepository(){
        return new InMemoryKademliaRepository();
    }

    @Bean
    public ROWConnectionInfo rowConnectionInfo(){
        return new ROWConnectionInfo(nodeProperties.getHost(), nodeProperties.getPort(), nodeProperties.isSsl());
    }

    @Bean
    @DependsOn({"rowNodeConnectionApi", "kademliaRepository", "rowConnectionInfo", "nodeInformation"})
    public KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Integer, String> kademliaSyncRepositoryNode(ROWNodeConnectionApi rowNodeConnectionApi, ROWConnectionInfo rowConnectionInfo, KademliaRepository<Integer, String> kademliaRepository, NodeInformation nodeInformation){
        KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Integer, String> node = new KademliaSyncRepositoryNode<>(nodeInformation.getId(), new BigIntegerRoutingTable<ROWConnectionInfo>(nodeInformation.getId()), rowNodeConnectionApi, rowConnectionInfo, kademliaRepository);
        node.setKademliaNodeListener(new RedistributionKademliaNodeListener<BigInteger, ROWConnectionInfo, Integer, String>(true, new RedistributionKademliaNodeListener.ShutdownDistributionListener<BigInteger, ROWConnectionInfo>() {
            @Override
            public void onFinish(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode) {

            }
        }));
        return node;
    }

}
