package com.github.ep2p.eleuth.service.listener;

import com.github.ep2p.eleuth.model.event.AvailabilityPublishEvent;
import com.github.ep2p.eleuth.service.route.RouteMessageSender;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

//listens to "AvailabilityPublishEvent" and forwards Availability message to other nodes
@Service
@Slf4j
public class AvailabilityPublishListener implements ApplicationListener<AvailabilityPublishEvent> {
    private final RouteMessageSender<ROWConnectionInfo> routeMessageSender;

    @Autowired
    public AvailabilityPublishListener(RouteMessageSender<ROWConnectionInfo> routeMessageSender) {
        this.routeMessageSender = routeMessageSender;
    }

    @Override
    public void onApplicationEvent(AvailabilityPublishEvent availabilityPublishEvent) {
        availabilityPublishEvent.getNodesToContact().forEach(nodeConnectionEntity -> {
            try {
                routeMessageSender.sendAvailabilityMessage(nodeConnectionEntity.getNodeId().toString(), nodeConnectionEntity, availabilityPublishEvent.getAvailabilityMessage());
            } catch (Exception e) {
                log.error("Failed to send request", e);
            }
        });
    }
}
