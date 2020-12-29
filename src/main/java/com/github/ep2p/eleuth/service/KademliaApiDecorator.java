package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.model.dto.kademlia.*;

public abstract class KademliaApiDecorator implements KademliaApi {
    private final KademliaApi kademliaApi;

    public KademliaApiDecorator(KademliaApi kademliaApi) {
        this.kademliaApi = kademliaApi;
    }

    @Override
    public PingResponse onPing(BasicRequest basicRequest) {
        return kademliaApi.onPing(basicRequest);
    }

    @Override
    public BasicResponse store(StoreRequest storeRequest) {
        return kademliaApi.store(storeRequest);
    }

    @Override
    public BasicResponse onShutdownSignal(BasicRequest basicRequest) {
        return kademliaApi.onShutdownSignal(basicRequest);
    }

    @Override
    public FindNodeResponse findNode(FindNodeRequest findNodeRequest) {
        return kademliaApi.findNode(findNodeRequest);
    }

    @Override
    public BasicResponse get(GetRequest getRequest) {
        return kademliaApi.get(getRequest);
    }

    @Override
    public BasicResponse onGetResult(GetResultRequest getResultRequest) {
        return kademliaApi.onGetResult(getResultRequest);
    }

    @Override
    public BasicResponse onStoreResult(StoreResultRequest storeResultRequest) {
        return kademliaApi.onStoreResult(storeResultRequest);
    }
}
