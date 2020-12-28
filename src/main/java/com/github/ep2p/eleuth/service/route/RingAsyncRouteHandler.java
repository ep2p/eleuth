package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.config.annotation.ConditionalOnRing;
import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.provider.SignedRingProofProvider;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.Pipeline;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@ConditionalOnRing
@Primary
@Service
@Slf4j
public class RingAsyncRouteHandler implements AsyncRouteHandler {
    private final Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline;
    private final RouteMessageSender<ROWConnectionInfo> routeMessageSender;
    private final SignedRingProofProvider signedRingProofProvider;
    private final MessageSignatureService messageSignatureService;

    public RingAsyncRouteHandler(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RouteMessageSender<ROWConnectionInfo> routeMessageSender, SignedRingProofProvider signedRingProofProvider, MessageSignatureService messageSignatureService) {
        this.availabilityPipeline = availabilityPipeline;
        this.routeMessageSender = routeMessageSender;
        this.signedRingProofProvider = signedRingProofProvider;
        this.messageSignatureService = messageSignatureService;
    }

    @SneakyThrows
    @Override
    public void onAvailabilityMessage(AvailabilityMessage availabilityMessage) {
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        SignedData<NodeDto> route = availabilityMessage.getMessage().getRoute();
        routeMessageSender.sendAvailabilityReply(route.getData().getId().toString(), route.getData().getConnectionInfo(), getAvailabilityResponse(availabilityMessage, output));
    }

    private AvailabilityReply getAvailabilityResponse(AvailabilityMessage availabilityMessage, AvailabilityOutput output) {
        AvailabilityReply.AvailabilityReplyBody availabilityReplyBody = new AvailabilityReply.AvailabilityReplyBody();
        availabilityReplyBody.setStatus(output.isFailed() ? BaseResponse.Status.FAIL : BaseResponse.Status.SUCCESS);
        availabilityReplyBody.setHit(true);
        availabilityReplyBody.setErrors(output.getErrorMessages());
        availabilityReplyBody.setRequestId(availabilityMessage.getMessage().getBody().getData().getRequestId());

        AvailabilityReply.AvailabilityReplyMessage availabilityReplyMessage = new AvailabilityReply.AvailabilityReplyMessage();
        availabilityReplyMessage.setBody(messageSignatureService.sign(availabilityReplyBody, true));
        availabilityReplyMessage.setRingProof(signedRingProofProvider.getRingProof());

        AvailabilityReply availabilityReply = new AvailabilityReply();
        availabilityReply.setReply(availabilityReplyMessage);
        return availabilityReply;
    }
}
