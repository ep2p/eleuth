package com.github.ep2p.eleuth.model.event;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailabilityPublishEvent {
    private AvailabilityMessage availabilityMessage;
    private List<? extends ConnectionInfo> nodes;
}
