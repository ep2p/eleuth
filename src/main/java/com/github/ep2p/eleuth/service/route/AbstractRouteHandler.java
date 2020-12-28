package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.MessageSignatureService;

public abstract class AbstractRouteHandler {
    private final MessageSignatureService messageSignatureService;

    protected AbstractRouteHandler(MessageSignatureService messageSignatureService) {
        this.messageSignatureService = messageSignatureService;
    }

    protected AvailabilityReply getAvailabilityResponse(AvailabilityMessage availabilityMessage, AvailabilityOutput output) {
        AvailabilityReply.AvailabilityReplyBody availabilityReplyBody = new AvailabilityReply.AvailabilityReplyBody();
        availabilityReplyBody.setStatus(output.isFailed() ? BaseResponse.Status.FAIL : BaseResponse.Status.SUCCESS);
        availabilityReplyBody.setHit(false);
        availabilityReplyBody.setErrors(output.getErrorMessages());
        availabilityReplyBody.setRequestId(availabilityMessage.getMessage().getBody().getData().getRequestId());

        AvailabilityReply.AvailabilityReplyMessage availabilityReplyMessage = new AvailabilityReply.AvailabilityReplyMessage();
        availabilityReplyMessage.setBody(messageSignatureService.sign(availabilityReplyBody, true));

        AvailabilityReply availabilityReply = new AvailabilityReply();
        availabilityReply.setReply(availabilityReplyMessage);
        return availabilityReply;
    }

}
