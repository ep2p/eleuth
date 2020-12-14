package com.github.ep2p.eleuth.model.event;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.entity.NodeConnectionEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailabilityPublishEvent {
    private AvailabilityMessage availabilityMessage;
    private List<NodeConnectionEntity> nodesToContact;
}
