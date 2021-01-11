package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.config.annotation.ConditionalOnRing;
import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
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
public class RingAsyncRouteHandler extends AbstractRouteHandler implements AsyncRouteHandler {
    private final Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline;
    private final RouteMessageSender<ROWConnectionInfo> routeMessageSender;
    private final SignedRingProofProvider signedRingProofProvider;

    public RingAsyncRouteHandler(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RouteMessageSender<ROWConnectionInfo> routeMessageSender, SignedRingProofProvider signedRingProofProvider, MessageSignatureService messageSignatureService) {
        super(messageSignatureService);
        this.availabilityPipeline = availabilityPipeline;
        this.routeMessageSender = routeMessageSender;
        this.signedRingProofProvider = signedRingProofProvider;
    }

    @SneakyThrows
    @Override
    public void onAvailabilityMessage(AvailabilityMessage availabilityMessage) {
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        SignedData<NodeDto> route = availabilityMessage.getMessage().getRoute();
        routeMessageSender.sendAvailabilityReply(route.getData().getId().toString(), route.getData().getConnectionInfo(), getAvailabilityResponse(availabilityMessage, output));
    }

    protected AvailabilityReply getAvailabilityResponse(AvailabilityMessage availabilityMessage, AvailabilityOutput output) {
        AvailabilityReply availabilityResponse = super.getAvailabilityResponse(availabilityMessage, output, true);
        availabilityResponse.getReply().setRingProof(signedRingProofProvider.getRingProof());
        return availabilityResponse;
    }
}
