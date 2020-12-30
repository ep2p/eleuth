package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.ProtocolMessage;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.util.Pipeline;

import javax.validation.Validator;

//verifies Availability message
public class AvailabilityVerificationStage extends AbstractVerificationStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final NodeInformation nodeInformation;

    public AvailabilityVerificationStage(Validator validator, MessageSignatureService messageSignatureService, NodeInformation nodeInformation) {
        super(validator, messageSignatureService);
        this.nodeInformation = nodeInformation;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {

        boolean validType = !availabilityMessage.getType().equals(ProtocolMessage.Type.AVAILABLE);
        boolean validFields = hasValidFields(availabilityMessage, o.getErrorMessages());
        boolean validSignatures = isValidSignature(availabilityMessage.getMessage().getBody()) && isValidSignature(availabilityMessage.getMessage().getRoute());

        if(!validType || !validFields || !validSignatures){
            o.setFailed(true);
            return false;
        }

        if(nodeInformation.getNodeType().equals(NodeType.RING))
            return availabilityMessage.getMessage().getRoute() != null;

        return true;
    }
}
