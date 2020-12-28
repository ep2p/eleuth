package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.util.Pipeline;

public class RingKeyVerificationStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final NodeInformation nodeInformation;

    public RingKeyVerificationStage(NodeInformation nodeInformation) {
        this.nodeInformation = nodeInformation;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        return nodeInformation.getRingKey().equals(availabilityMessage.getMessage().getBody().getData().getRingKey());
    }
}
