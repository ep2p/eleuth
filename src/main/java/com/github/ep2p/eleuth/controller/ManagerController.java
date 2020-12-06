package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.exception.BootstrapException;
import com.github.ep2p.kademlia.exception.GetException;
import com.github.ep2p.kademlia.exception.StoreException;
import com.github.ep2p.kademlia.model.GetAnswer;
import com.github.ep2p.kademlia.model.StoreAnswer;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.node.Node;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Profile("manager")
@ConditionalOnProperty(prefix = "config", name = "nodeType", havingValue = "RING")
public class ManagerController {
    private final KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaSyncRepositoryNode;

    public ManagerController(KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaSyncRepositoryNode) {
        this.kademliaSyncRepositoryNode = kademliaSyncRepositoryNode;
    }

    @GetMapping("/manager/start")
    public @ResponseBody
    String start(){
        kademliaSyncRepositoryNode.start();
        return "OK";
    }

    @PostMapping("/manager/bootstrap")
    public @ResponseBody
    String bootstrap(@RequestBody Node<BigInteger, ROWConnectionInfo> bootstrapNode){
        try {
            this.kademliaSyncRepositoryNode.bootstrap(bootstrapNode);
            return "OK!";
        } catch (BootstrapException e) {
            log.error("Failed to bootstrap node", e);
            return "FAILED";
        }
    }

    @PostMapping("/manager/store")
    public @ResponseBody
    String store(@RequestBody ManagerStore managerStore){
        try {
            Key builtKey = Key.builder()
                    .type(Key.Type.MESSAGE)
                    .value(String.valueOf(managerStore.getKey()))
                    .build();
            StoreAnswer<BigInteger, Key> storeAnswer = this.kademliaSyncRepositoryNode.store(builtKey, managerStore.getValue(), 10, TimeUnit.SECONDS);
            return "Node #"+ storeAnswer.getNodeId() + " STORED DATA";
        } catch (StoreException | InterruptedException e) {
            e.printStackTrace();
            return "Failed to store: " + e.getMessage();
        }
    }

    @GetMapping("/manager/get/{key}")
    public @ResponseBody
    String get(@PathVariable Integer key){
        try {
            Key builtKey = Key.builder()
                    .type(Key.Type.MESSAGE)
                    .value(String.valueOf(key))
                    .build();
            GetAnswer<BigInteger, Key, String> getAnswer = this.kademliaSyncRepositoryNode.get(builtKey, 10, TimeUnit.SECONDS);
            return "Node #"+getAnswer.getNodeId() + " GOT DATA: " + getAnswer.getValue();
        } catch (GetException e) {
            e.printStackTrace();
            return "Failed to get: " + e.getMessage();
        }
    }

    @Getter
    @Setter
    public static class ManagerStore {
        private int key;
        private String value;
    }
}
