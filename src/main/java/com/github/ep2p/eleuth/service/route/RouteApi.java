package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;

public interface RouteApi {
    BaseResponse onAvailabilityMessage(AvailabilityMessage availabilityMessage);
    BaseResponse onAvailabilityReply(AvailabilityReply availabilityReply);
}
