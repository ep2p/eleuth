package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.route.RouteApi;
import lab.idioglossia.row.server.annotations.RowController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RowController
public class RouteController {
    private final RouteApi routeApi;

    public RouteController(RouteApi routeApi) {
        this.routeApi = routeApi;
    }

    @PostMapping("/route/request/availability")
    public @ResponseBody
    BaseResponse available(@RequestBody AvailabilityMessage availabilityMessage){
        return routeApi.onAvailabilityMessage(availabilityMessage);
    }

    @PostMapping("/route/reply/availability")
    public @ResponseBody
    BaseResponse availableReply(@RequestBody AvailabilityReply availabilityReply){
        return routeApi.onAvailabilityReply(availabilityReply);
    }

}
