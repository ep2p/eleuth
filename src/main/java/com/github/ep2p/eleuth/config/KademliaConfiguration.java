package com.github.ep2p.eleuth.config;

import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.EleuthKademliaRepository;
import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.ROWNodeConnectionApi;
import com.github.ep2p.kademlia.node.KademliaNode;
import com.github.ep2p.kademlia.node.KademliaRepository;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.node.RedistributionKademliaNodeListener;
import com.github.ep2p.kademlia.table.BigIntegerRoutingTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.math.BigInteger;

@Configuration
@ConditionalOnProperty(prefix = "config", name = "nodeType", havingValue = "RING")
@EnableConfigurationProperties({NodeProperties.class})
@Slf4j
public class KademliaConfiguration {
    private final NodeProperties nodeProperties;

    public KademliaConfiguration(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
        log.info("Node: "+ nodeProperties.toString());
    }

    @Bean
    public KademliaRepository<Key,String> kademliaRepository(){
        return new EleuthKademliaRepository();
    }

    @Bean
    public ROWConnectionInfo rowConnectionInfo(){
        return new ROWConnectionInfo(nodeProperties.getHost(), nodeProperties.getPort(), nodeProperties.isSsl());
    }

    @Bean("kademliaNode")
    @DependsOn({"rowNodeConnectionApi", "kademliaRepository", "rowConnectionInfo", "nodeInformation"})
    public KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode(ROWNodeConnectionApi rowNodeConnectionApi, ROWConnectionInfo rowConnectionInfo, KademliaRepository<Key, String> kademliaRepository, NodeInformation nodeInformation){
        KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> node = new KademliaSyncRepositoryNode<>(nodeInformation.getId(), new BigIntegerRoutingTable<ROWConnectionInfo>(nodeInformation.getId()), rowNodeConnectionApi, rowConnectionInfo, kademliaRepository);
        node.setKademliaNodeListener(new EleuthKademliaNodeListenerDecorator(new RedistributionKademliaNodeListener<BigInteger, ROWConnectionInfo, Key, String>(true, new RedistributionKademliaNodeListener.ShutdownDistributionListener<BigInteger, ROWConnectionInfo>() {
            @Override
            public void onFinish(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode) {

            }
        })));
        node.start();
        rowNodeConnectionApi.init(node);
        return node;
    }

}
