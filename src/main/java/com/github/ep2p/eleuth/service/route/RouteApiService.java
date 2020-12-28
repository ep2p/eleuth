package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.RequestCacheService;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//handles Eleuth routed messages
@Service
@Slf4j
public class RouteApiService {
    private final AsyncRouteHandler asyncRouteHandler;
    private final RequestCacheService requestCacheService;
    private final MessageSignatureService messageSignatureService;
    private final RowConnectionPool rowConnectionPool;

    @Autowired
    public RouteApiService(AsyncRouteHandler asyncRouteHandler, RequestCacheService requestCacheService, MessageSignatureService messageSignatureService, RowConnectionPool rowConnectionPool) {
        this.asyncRouteHandler = asyncRouteHandler;
        this.requestCacheService = requestCacheService;
        this.messageSignatureService = messageSignatureService;
        this.rowConnectionPool = rowConnectionPool;
    }

    public BaseResponse onAvailabilityMessage(AvailabilityMessage availabilityMessage){
        asyncRouteHandler.onAvailabilityMessage(availabilityMessage);
        return new BaseResponse(BaseResponse.Status.SUCCESS);
    }

    @SneakyThrows
    public BaseResponse onAvailabilityReply(AvailabilityReply availabilityReply){
        AvailabilityReply.AvailabilityReplyMessage replyMessage = availabilityReply.getReply();
        String requestId = replyMessage.getBody().getData().getRequestId();
        AvailabilityMessage cachedAvailabilityMessage = requestCacheService.getAvailabilityMessage(requestId);
        if(cachedAvailabilityMessage == null)
            return new BaseResponse(BaseResponse.Status.FAIL);

        try {
            messageSignatureService.validate(replyMessage.getBody());
        } catch (InvalidSignatureException e) {
            return new BaseResponse(BaseResponse.Status.FAIL);
        }

        SignedData<AvailabilityReply.AvailabilityReplyBody> signedData = messageSignatureService.sign(replyMessage.getBody().getData(), true);
        replyMessage.setBody(signedData);

        if (cachedAvailabilityMessage.getMessage().getRoute() == null) {
            //todo: forward reply to the client!
        }else {
            //forwarding reply to requester proxy

            SignedData<NodeDto> route = cachedAvailabilityMessage.getMessage().getRoute();
            RowClient client = rowConnectionPool.getClient(route.getData().getId().toString(), route.getData().getConnectionInfo());
            client.sendRequest(RowRequest.<AvailabilityReply, Void>builder()
                    .method(RowRequest.RowMethod.POST)
                    .address("") //todo
                    .body(availabilityReply)
                    .build(), new ResponseCallback<BaseResponse>(BaseResponse.class) {
                @Override
                public void onResponse(RowResponse<BaseResponse> rowResponse) {

                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("Failed to send AvailabilityReply");
                }
            });
        }

        requestCacheService.evictAvailability(requestId);
        return new BaseResponse();

    }

}
