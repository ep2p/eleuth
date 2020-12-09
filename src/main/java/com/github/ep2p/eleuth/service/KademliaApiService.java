package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.config.annotation.ConditionalOnRing;
import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.kademlia.*;
import com.github.ep2p.eleuth.repository.Key;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.exception.NodeIsOfflineException;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import com.github.ep2p.kademlia.node.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Date;

@Service
@ConditionalOnRing
@Slf4j
public class KademliaApiService {
    private final KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode;
    private final MessageSignatureService messageSignatureService;
    private final NodeValidatorService nodeValidatorService;
    private SignedData<NodeDto> nodeDto;

    @Autowired
    public KademliaApiService(KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode, MessageSignatureService messageSignatureService, NodeValidatorService nodeValidatorService) {
        this.kademliaNode = kademliaNode;
        this.messageSignatureService = messageSignatureService;
        this.nodeValidatorService = nodeValidatorService;
    }

    @PostConstruct
    public void init(){
        this.nodeDto = messageSignatureService.sign(NodeDto.builder()
                .connectionInfo(kademliaNode.getConnectionInfo())
                .id(kademliaNode.getId())
                .type(NodeType.RING)
                .build(), true);
    }

    private Node<BigInteger, ROWConnectionInfo> getNodeFromDto(NodeDto nodeDto){
        return new Node<BigInteger, ROWConnectionInfo>(nodeDto.getId(), nodeDto.getConnectionInfo(), new Date());
    }

    public PingResponse onPing(BasicRequest basicRequest){
        try {
            validate(basicRequest.getCaller());
            PingAnswer<BigInteger> pingAnswer = kademliaNode.onPing(getNodeFromDto(basicRequest.getCaller().getData()));
            return new PingResponse(this.nodeDto, pingAnswer);
        } catch (NodeIsOfflineException e) {
            return new PingResponse(this.nodeDto, new PingAnswer<>(kademliaNode.getId(), false));
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
            return new PingResponse();
        }
    }

    public BasicResponse store(StoreRequest storeRequest){
        try {
            validate(storeRequest.getCaller());
            kademliaNode.onStoreRequest(getNodeFromDto(storeRequest.getCaller().getData()), getNodeFromDto(storeRequest.getRequester()), storeRequest.getKey(), storeRequest.getValue());
            return new BasicResponse(this.nodeDto);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }


    public BasicResponse onShutdownSignal(BasicRequest basicRequest){
        try {
            validate(basicRequest.getCaller());
            kademliaNode.onShutdownSignal(getNodeFromDto(basicRequest.getCaller().getData()));
            return new BasicResponse(this.nodeDto);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public FindNodeResponse findNode(FindNodeRequest findNodeRequest){
        try {
            validate(findNodeRequest.getCaller());
            FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = kademliaNode.onFindNode(getNodeFromDto(findNodeRequest.getCaller().getData()), findNodeRequest.getLookupId());
            return new FindNodeResponse(this.nodeDto, findNodeAnswer);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        } catch (NodeIsOfflineException ignored) {}
        return new FindNodeResponse();
    }

    public BasicResponse get(GetRequest getRequest){
        try {
            validate(getRequest.getCaller());
            kademliaNode.onGetRequest(getNodeFromDto(getRequest.getCaller().getData()), getNodeFromDto(getRequest.getRequester()), getRequest.getKey());
            return new BasicResponse(this.nodeDto);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public BasicResponse onGetResult(GetResultRequest getResultRequest){
        try {
            validate(getResultRequest.getCaller());
            kademliaNode.onGetResult(getNodeFromDto(getResultRequest.getCaller().getData()), getResultRequest.getKey(), getResultRequest.getValue());
            return new BasicResponse(this.nodeDto);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public BasicResponse onStoreResult(StoreResultRequest storeResultRequest){
        try {
            validate(storeResultRequest.getCaller());
            kademliaNode.onStoreResult(getNodeFromDto(storeResultRequest.getCaller().getData()), storeResultRequest.getKey(), storeResultRequest.isSuccess());
            return new BasicResponse(this.nodeDto);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    private void validate(SignedData<NodeDto> caller) throws InvalidSignatureException {
//        RowUser rowUser = RowContextHolder.getContext().getRowUser();
//        rowSessionRegistry.getSession(rowUser.getUserId(), rowUser.getSessionId()).getSession().close();

        if (!nodeValidatorService.isValidRingNode(caller)) {
            throw new InvalidSignatureException();
        }
    }
}
