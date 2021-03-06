package com.github.ep2p.eleuth.model.event;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.entity.file.NodeConnectionEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class AvailabilityPublishEvent extends ApplicationEvent {
    private AvailabilityMessage availabilityMessage;
    private List<NodeConnectionEntity> nodesToContact;

    public AvailabilityPublishEvent(Object source) {
        super(source);
    }
}
