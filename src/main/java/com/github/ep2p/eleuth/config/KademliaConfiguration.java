package com.github.ep2p.eleuth.config;

import com.github.ep2p.eleuth.config.annotation.ConditionalOnRing;
import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.EleuthKademliaRepository;
import com.github.ep2p.eleuth.repository.RoutingTableLoader;
import com.github.ep2p.eleuth.service.CertificateCollectorNodeConnectionApiDecorator;
import com.github.ep2p.eleuth.service.EleuthKademliaRepositoryNode;
import com.github.ep2p.eleuth.service.NodeValidatorService;
import com.github.ep2p.eleuth.service.provider.SignedNodeDtoProvider;
import com.github.ep2p.eleuth.service.provider.SignedRingProofProvider;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.ROWNodeConnectionApi;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import com.github.ep2p.encore.helper.KeyStoreWrapper;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.node.KademliaNode;
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
    private final EleuthKademliaRepository kademliaRepository;
    private KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode;

    public KademliaConfiguration(RoutingTableLoader routingTableLoader, EleuthKademliaRepository kademliaRepository) {
        this.routingTableLoader = routingTableLoader;
        this.kademliaRepository = kademliaRepository;
    }



    @Bean("routingTable")
    @DependsOn("nodeInformation")
    public BigIntegerRoutingTable<ROWConnectionInfo> routingTable(NodeInformation nodeInformation){
        BigIntegerRoutingTable<ROWConnectionInfo> bigIntegerRoutingTable = routingTableLoader.get();
        if(bigIntegerRoutingTable != null)
            return bigIntegerRoutingTable;

        return new BigIntegerRoutingTable<ROWConnectionInfo>(nodeInformation.getId());
    }

    @Bean("rowNodeConnectionApi")
    @DependsOn({"rowConnectionPool", "signedNodeDtoProvider", "signedRingProofProvider", "nodeValidatorService", "keyStoreWrapper, nodeInformation"})
    public NodeConnectionApi<BigInteger, ROWConnectionInfo> nodeConnectionApi(RowConnectionPool rowConnectionPool, SignedNodeDtoProvider signedNodeDtoProvider, SignedRingProofProvider signedRingProofProvider, NodeValidatorService nodeValidatorService, KeyStoreWrapper keyStoreWrapper, NodeInformation nodeInformation){
        ROWNodeConnectionApi rowNodeConnectionApi = new ROWNodeConnectionApi(rowConnectionPool, signedNodeDtoProvider, signedRingProofProvider, nodeValidatorService);
        return new CertificateCollectorNodeConnectionApiDecorator(rowNodeConnectionApi, keyStoreWrapper, nodeInformation, userIdGenerator);
    }

    @Bean("kademliaNode")
    @DependsOn({"rowNodeConnectionApi", "kademliaRepository", "rowConnectionInfo", "nodeInformation", "routingTable"})
    public EleuthKademliaRepositoryNode kademliaNode(NodeConnectionApi<BigInteger, ROWConnectionInfo> rowNodeConnectionApi, ROWConnectionInfo rowConnectionInfo, NodeInformation nodeInformation, BigIntegerRoutingTable<ROWConnectionInfo> routingTable) throws IOException {
        EleuthKademliaRepositoryNode node = new EleuthKademliaRepositoryNode(nodeInformation.getId(), routingTable, rowNodeConnectionApi, rowConnectionInfo, kademliaRepository);
        node.setKademliaNodeListener(new EleuthKademliaNodeListenerDecorator(new RedistributionKademliaNodeListener<BigInteger, ROWConnectionInfo, Key, String>(true, new RedistributionKademliaNodeListener.ShutdownDistributionListener<BigInteger, ROWConnectionInfo>() {
            @Override
            public void onFinish(KademliaNode<BigInteger, ROWConnectionInfo> kademliaNode) {

            }
        })));
        node.start();
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
