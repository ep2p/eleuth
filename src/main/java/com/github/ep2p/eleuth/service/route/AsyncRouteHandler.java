package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;

public interface AsyncRouteHandler {
    void onAvailabilityMessage(AvailabilityMessage availabilityMessage);
}
