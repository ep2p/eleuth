package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.service.RequestCacheService;
import com.github.ep2p.eleuth.util.Pipeline;

import java.util.UUID;

//calls request cache service to cache the request for Availability message
public class AvailabilityRequestCacheStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final RequestCacheService requestCacheService;

    public AvailabilityRequestCacheStage(RequestCacheService requestCacheService) {
        this.requestCacheService = requestCacheService;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        String requestId = availabilityMessage.getMessage().getBody().getData().getRequestId();
        try{
            UUID.fromString(requestId);
        } catch (IllegalArgumentException exception){
            o.getErrorMessages().add(exception.getMessage());
            o.setFailed(true);
            return false;
        }
        requestCacheService.addAvailabilityRequest(requestId, availabilityMessage);
        return true;
    }
}
