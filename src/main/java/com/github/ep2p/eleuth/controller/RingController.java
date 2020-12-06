package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.model.dto.kademlia.GetRequest;
import com.github.ep2p.eleuth.model.dto.kademlia.*;
import com.github.ep2p.eleuth.service.KademliaApiService;
import lab.idioglossia.row.annotations.RowController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RowController
public class RingController {
    private final KademliaApiService kademliaApiService;

    @Autowired
    public RingController(KademliaApiService kademliaApiService) {
        this.kademliaApiService = kademliaApiService;
    }

    @PutMapping("/ring/ping")
    public @ResponseBody
    PingResponse onPing(@RequestBody BasicRequest basicRequest) {
        return kademliaApiService.onPing(basicRequest);
    }

    @PostMapping("/ring/shutdown-signal")
    public @ResponseBody
    BasicResponse onShutdownSignal(@RequestBody BasicRequest basicRequest){
        return kademliaApiService.onShutdownSignal(basicRequest);
    }

    @PostMapping("/ring/find")
    public @ResponseBody
    FindNodeResponse findNode(@RequestBody FindNodeRequest findNodeRequest) {
        return kademliaApiService.findNode(findNodeRequest);
    }

    @PostMapping("/ring/store")
    public @ResponseBody
    BasicResponse store(@RequestBody StoreRequest storeRequest){
        return kademliaApiService.store(storeRequest);
    }

    @PostMapping("/ring/get")
    public @ResponseBody
    BasicResponse get(@RequestBody GetRequest getRequest){
        return kademliaApiService.get(getRequest);
    }

    @PostMapping("/ring/get/result")
    public @ResponseBody BasicResponse onGetResult(@RequestBody GetResultRequest getResultRequest){
        return kademliaApiService.onGetResult(getResultRequest);
    }

    @PostMapping("/ring/store/result")
    public @ResponseBody BasicResponse onStoreResult(@RequestBody StoreResultRequest storeResultRequest){
        return kademliaApiService.onStoreResult(storeResultRequest);
    }
}
