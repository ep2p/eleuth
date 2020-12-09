package com.github.ep2p.eleuth.service.row;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.kademlia.*;
import com.github.ep2p.eleuth.model.entity.Key;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.NodeValidatorService;
import com.github.ep2p.eleuth.service.SignedNodeDtoProviderService;
import com.github.ep2p.kademlia.connection.NodeConnectionApi;
import com.github.ep2p.kademlia.exception.StoreException;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import com.github.ep2p.kademlia.model.PingAnswer;
import com.github.ep2p.kademlia.node.Node;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service("rowNodeConnectionApi")
@Slf4j
public class ROWNodeConnectionApi implements NodeConnectionApi<BigInteger, ROWConnectionInfo> {
    private final RowConnectionPool rowConnectionPool;
    private final SignedNodeDtoProviderService signedNodeDtoProviderService;
    private final NodeValidatorService nodeValidatorService;

    @Autowired
    public ROWNodeConnectionApi(RowConnectionPool rowConnectionPool, SignedNodeDtoProviderService signedNodeDtoProviderService, NodeValidatorService nodeValidatorService) {
        this.rowConnectionPool = rowConnectionPool;
        this.signedNodeDtoProviderService = signedNodeDtoProviderService;
        this.nodeValidatorService = nodeValidatorService;
    }


    @Override
    public PingAnswer<BigInteger> ping(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node) {
        RowRequest<BasicRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.PUT, "/ring/ping", null, new BasicRequest(signedNodeDtoProviderService.getWithCertificate()), new HashMap<>());

        AtomicReference<PingAnswer<BigInteger>> responseAtomicAnswer = new AtomicReference<>(new PingAnswer<>(node.getId(), false));
        CountDownLatch latch = new CountDownLatch(1);
        try {
            RowClient client = rowConnectionPool.getClient(node.getConnectionInfo());
            client.sendRequest(request, new ResponseCallback<PingResponse>(PingResponse.class) {
                @Override
                public void onResponse(RowResponse<PingResponse> rowResponse) {
                    try {
                        validate(rowResponse.getBody().getNode());
                        responseAtomicAnswer.set(rowResponse.getBody().getPingAnswer());
                    } catch (InvalidSignatureException e) {
                        log.error("Could not validate signatures inside response", e);
                    }finally {
                        latch.countDown();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    latch.countDown();
                }
            });
        } catch (Exception e) {
            latch.countDown();
            log.error("Failed to send request", e);
        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Response timeout", e);
        }
        return responseAtomicAnswer.get();
    }

    @Override
    public void shutdownSignal(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node) {
        RowRequest<BasicRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/shutdown-signal", null, new BasicRequest(signedNodeDtoProviderService.getWithCertificate()), new HashMap<>());
        try {
            rowConnectionPool.getClient(node.getConnectionInfo()).sendRequest(request, new ResponseCallback<BasicResponse>(BasicResponse.class) {
                @Override
                public void onResponse(RowResponse<BasicResponse> rowResponse) {}

                @Override
                public void onError(Throwable throwable) {}
            });
        } catch (IOException e) {
            log.error("Failed send request", e);
        }
    }

    @Override
    public FindNodeAnswer<BigInteger, ROWConnectionInfo> findNode(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> node, BigInteger nodeId) {
        RowRequest<FindNodeRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/find", null, new FindNodeRequest(signedNodeDtoProviderService.getWithCertificate(), nodeId), new HashMap<>());
        FindNodeAnswer<BigInteger, ROWConnectionInfo> defaultAnswer = new FindNodeAnswer<BigInteger, ROWConnectionInfo>(BigInteger.valueOf(0));
        defaultAnswer.setAlive(false);
        AtomicReference<FindNodeAnswer<BigInteger, ROWConnectionInfo>> responseAtomicAnswer = new AtomicReference<>(defaultAnswer);
        CountDownLatch latch = new CountDownLatch(1);
        try {
            rowConnectionPool.getClient(node.getConnectionInfo()).sendRequest(request, new ResponseCallback<FindNodeResponse>(FindNodeResponse.class) {
                @Override
                public void onResponse(RowResponse<FindNodeResponse> rowResponse) {
                    responseAtomicAnswer.set(rowResponse.getBody().getAnswer());
                    latch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("", throwable);
                    latch.countDown();
                }
            });
        } catch (IOException e) {
            latch.countDown();
            log.error("Failed to send request", e);
        }
        try {
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Response timeout", e);
        }
        return responseAtomicAnswer.get();
    }

    //not async yet
    @Override
    public <K, V> void storeAsync(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key, V value) {
        StoreRequest storeRequest = new StoreRequest(signedNodeDtoProviderService.getWithCertificate(), requester);
        storeRequest.setKey(getKey(key));
        storeRequest.setValue(getValue(value));
        RowRequest<StoreRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/store", null, storeRequest, new HashMap<>());
        try {
            rowConnectionPool.getClient(node.getConnectionInfo()).sendRequest(request, new ResponseCallback<BasicResponse>(BasicResponse.class) {
                @Override
                public void onResponse(RowResponse<BasicResponse> rowResponse) {
                    try {
                        validate(rowResponse.getBody().getNode());
                    } catch (InvalidSignatureException e) {
                        log.error("Could not validate signatures inside response", e);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new RuntimeException(new StoreException(throwable.getMessage()));
                }
            });
        } catch (IOException e) {
            log.error("Failed send request", e);
        }
    }

    @Override
    public <K> void getRequest(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, Node<BigInteger, ROWConnectionInfo> node, K key) {
        GetRequest getRequest = new GetRequest(signedNodeDtoProviderService.getWithCertificate(), requester);
        getRequest.setKey(getKey(key));
        RowRequest<GetRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/get", null, getRequest, new HashMap<>());
        try {
            rowConnectionPool.getClient(node.getConnectionInfo()).sendRequest(request, new ResponseCallback<BasicResponse>(BasicResponse.class) {
                @Override
                public void onResponse(RowResponse<BasicResponse> rowResponse) {
                    try {
                        validate(rowResponse.getBody().getNode());
                    } catch (InvalidSignatureException e) {
                        log.error("Could not validate signatures inside response", e);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new RuntimeException(new StoreException(throwable.getMessage()));
                }
            });
        } catch (IOException e) {
            log.error("Failed send request", e);
        }
    }

    @Override
    public <K, V> void sendGetResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, V value) {
        GetResultRequest getResultRequest = new GetResultRequest(signedNodeDtoProviderService.getWithCertificate());
        getResultRequest.setKey(getKey(key));
        getResultRequest.setValue(getValue(value));
        RowRequest<GetResultRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/get/result", null, getResultRequest, new HashMap<>());
        try {
            rowConnectionPool.getClient(requester.getConnectionInfo()).sendRequest(request, new ResponseCallback<BasicResponse>(BasicResponse.class) {
                @Override
                public void onResponse(RowResponse<BasicResponse> rowResponse) {
                    try {
                        validate(rowResponse.getBody().getNode());
                    } catch (InvalidSignatureException e) {
                        log.error("Could not validate signatures inside response", e);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("Failed to send get results to " + requester.getId(), throwable);
                }
            });
        } catch (IOException e){
            log.error("Failed send request", e);
        }
    }

    @Override
    public <K> void sendStoreResults(Node<BigInteger, ROWConnectionInfo> caller, Node<BigInteger, ROWConnectionInfo> requester, K key, boolean success) {
        StoreResultRequest storeResultRequest = new StoreResultRequest(signedNodeDtoProviderService.getWithCertificate());
        storeResultRequest.setKey(getKey(key));
        storeResultRequest.setSuccess(success);
        RowRequest<StoreResultRequest, Void> request = new RowRequest<>(RowRequest.RowMethod.POST, "/ring/store/result", null, storeResultRequest, new HashMap<>());
        try {
            rowConnectionPool.getClient(requester.getConnectionInfo()).sendRequest(request, new ResponseCallback<BasicResponse>(BasicResponse.class) {
                @Override
                public void onResponse(RowResponse<BasicResponse> rowResponse) {
                    try {
                        validate(rowResponse.getBody().getNode());
                    } catch (InvalidSignatureException e) {
                        log.error("Could not validate signatures inside response", e);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new RuntimeException(new StoreException(throwable.getMessage()));
                }
            });
        } catch (IOException e) {
            log.error("Failed send request", e);
        }
    }

    private void validate(SignedData<NodeDto> signedData) throws InvalidSignatureException {
        if (!nodeValidatorService.isValidRingNode(signedData)) {
            throw new InvalidSignatureException();
        }
    }

    private Key getKey(Object k){
        assert k instanceof Key;
        return (Key) k;
    }

    private String getValue(Object v){
        assert v instanceof String;
        return (String) v;
    }
}
