package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.exception.InvalidSignatureException;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.RequestCacheService;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
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
    private final RouteMessageSender<ROWConnectionInfo> routeMessageSender;

    @Autowired
    public RouteApiService(AsyncRouteHandler asyncRouteHandler, RequestCacheService requestCacheService, MessageSignatureService messageSignatureService, RouteMessageSender<ROWConnectionInfo> routeMessageSender) {
        this.asyncRouteHandler = asyncRouteHandler;
        this.requestCacheService = requestCacheService;
        this.messageSignatureService = messageSignatureService;
        this.routeMessageSender = routeMessageSender;
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
            routeMessageSender.sendAvailabilityReply(route.getData().getId().toString(), route.getData().getConnectionInfo(), availabilityReply);
        }

        requestCacheService.evictAvailability(requestId);
        return new BaseResponse();

    }

}
