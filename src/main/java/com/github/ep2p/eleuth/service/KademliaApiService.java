package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.kademlia.*;
import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.service.provider.SignedNodeDtoProvider;
import com.github.ep2p.eleuth.service.provider.SignedRingProofProvider;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.exception.NodeIsOfflineException;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.KademliaSyncRepositoryNode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

import static com.github.ep2p.eleuth.util.NodeUtil.getNodeFromDto;

//redirects kademlia api inputs to the kademlia node
@Slf4j
public class KademliaApiService implements KademliaApi {
    private final KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode;
    private final NodeValidatorService nodeValidatorService;
    private final SignedRingProofProvider signedRingProofProvider;
    private final SignedNodeDtoProvider signedNodeDtoProvider;
    private SignedData<NodeDto> nodeDto;
    private SignedData<RingMemberProofDto> ringProof;


    public KademliaApiService(KademliaSyncRepositoryNode<BigInteger, ROWConnectionInfo, Key, String> kademliaNode, NodeValidatorService nodeValidatorService, SignedRingProofProvider signedRingProofProvider, SignedNodeDtoProvider signedNodeDtoProvider) {
        this.kademliaNode = kademliaNode;
        this.nodeValidatorService = nodeValidatorService;
        this.signedRingProofProvider = signedRingProofProvider;
        this.signedNodeDtoProvider = signedNodeDtoProvider;
    }

    @PostConstruct
    public void init(){
        this.nodeDto = signedNodeDtoProvider.getWithCertificate();
        this.ringProof = signedRingProofProvider.getRingProof();
    }


    public PingResponse onPing(BasicRequest basicRequest){
        try {
            validate(basicRequest.getCaller(), basicRequest.getRingProof());
            PingAnswer<BigInteger> pingAnswer = kademliaNode.onPing(getNodeFromDto(basicRequest.getCaller().getData()));
            return new PingResponse(this.nodeDto, this.ringProof, pingAnswer);
        } catch (NodeIsOfflineException e) {
            return new PingResponse(this.nodeDto, this.ringProof, new PingAnswer<>(kademliaNode.getId(), false));
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
            return new PingResponse();
        }
    }

    public BasicResponse store(StoreRequest storeRequest){
        try {
            validate(storeRequest.getCaller(), storeRequest.getRingProof());
            validate(storeRequest);
            kademliaNode.onStoreRequest(getNodeFromDto(storeRequest.getCaller().getData()), getNodeFromDto(storeRequest.getRequester()), storeRequest.getKey(), storeRequest.getValue());
            return new BasicResponse(this.nodeDto, this.ringProof);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    //todo
    private void validate(StoreRequest storeRequest) {

    }


    public BasicResponse onShutdownSignal(BasicRequest basicRequest){
        try {
            validate(basicRequest.getCaller(), basicRequest.getRingProof());
            kademliaNode.onShutdownSignal(getNodeFromDto(basicRequest.getCaller().getData()));
            return new BasicResponse(this.nodeDto, this.ringProof);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public FindNodeResponse findNode(FindNodeRequest findNodeRequest){
        try {
            validate(findNodeRequest.getCaller(), findNodeRequest.getRingProof());
            FindNodeAnswer<BigInteger, ROWConnectionInfo> findNodeAnswer = kademliaNode.onFindNode(getNodeFromDto(findNodeRequest.getCaller().getData()), findNodeRequest.getLookupId());
            return new FindNodeResponse(this.nodeDto, this.ringProof, findNodeAnswer);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        } catch (NodeIsOfflineException ignored) {}
        return new FindNodeResponse();
    }

    public BasicResponse get(GetRequest getRequest){
        try {
            validate(getRequest.getCaller(), getRequest.getRingProof());
            kademliaNode.onGetRequest(getNodeFromDto(getRequest.getCaller().getData()), getNodeFromDto(getRequest.getRequester()), getRequest.getKey());
            return new BasicResponse(this.nodeDto, this.ringProof);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public BasicResponse onGetResult(GetResultRequest getResultRequest){
        try {
            validate(getResultRequest.getCaller(), getResultRequest.getRingProof());
            kademliaNode.onGetResult(getNodeFromDto(getResultRequest.getCaller().getData()), getResultRequest.getKey(), getResultRequest.getValue());
            return new BasicResponse(this.nodeDto, this.ringProof);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    public BasicResponse onStoreResult(StoreResultRequest storeResultRequest){
        try {
            validate(storeResultRequest.getCaller(), storeResultRequest.getRingProof());
            kademliaNode.onStoreResult(getNodeFromDto(storeResultRequest.getCaller().getData()), storeResultRequest.getKey(), storeResultRequest.isSuccess());
            return new BasicResponse(this.nodeDto, this.ringProof);
        } catch (InvalidSignatureException e) {
            log.error("Invalid signature of caller", e);
        }
        return new BasicResponse();
    }

    private void validate(SignedData<NodeDto> caller, SignedData<RingMemberProofDto> memberProof) throws InvalidSignatureException {
//        RowUser rowUser = RowContextHolder.getContext().getRowUser();
//        rowSessionRegistry.getSession(rowUser.getUserId(), rowUser.getSessionId()).getSession().close();
        //todo: can validate once and add to session to avoid validation on all ROW request
        //todo note: requests might be HTTP (ROW Fallback) so first request type should be checked
        if (!nodeValidatorService.isValidRingNode(caller, memberProof)) {
            throw new InvalidSignatureException();
        }
    }
}
