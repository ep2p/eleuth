package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//handles Eleuth routed messages
@Service
public class RouteApiService {
    private final AsyncRouteHandler asyncRouteHandler;

    @Autowired
    public RouteApiService(AsyncRouteHandler asyncRouteHandler) {
        this.asyncRouteHandler = asyncRouteHandler;
    }

    public BaseResponse onAvailabilityMessage(AvailabilityMessage availabilityMessage){
        asyncRouteHandler.onAvailabilityMessage(availabilityMessage);
        return new BaseResponse(BaseResponse.Status.SUCCESS);
    }

}
