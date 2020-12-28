package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.kademlia.connection.ConnectionInfo;

public interface RouteMessageSender<C extends ConnectionInfo> {
    void sendAvailabilityMessage(String nodeId, C connectionInfo, AvailabilityMessage availabilityMessage) throws Exception;
    void sendAvailabilityReply(String nodeId, C connectionInfo, AvailabilityReply availabilityReply) throws Exception;
    void sendAvailabilityReply(String nodeId, AvailabilityReply availabilityReply);
}
