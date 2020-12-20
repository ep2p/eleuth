package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.entity.NodeConnectionEntity;
import com.github.ep2p.eleuth.model.event.AvailabilityPublishEvent;
import com.github.ep2p.eleuth.repository.NodeConnectionRepository;
import com.github.ep2p.eleuth.service.SignedNodeDtoProviderService;
import com.github.ep2p.eleuth.util.Pipeline;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AvailabilityDistributionStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final NodeConnectionRepository nodeConnectionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SignedNodeDtoProviderService signedNodeDtoProviderService;

    public AvailabilityDistributionStage(NodeConnectionRepository nodeConnectionRepository, ApplicationEventPublisher applicationEventPublisher, SignedNodeDtoProviderService signedNodeDtoProviderService) {
        this.nodeConnectionRepository = nodeConnectionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.signedNodeDtoProviderService = signedNodeDtoProviderService;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        BigInteger ringId = availabilityMessage.getMessage().getBody().getData().getRingId();
        List<NodeConnectionEntity> nodeConnectionEntities = nodeConnectionRepository.findAllByRingId(ringId);
        if(nodeConnectionEntities.size() == 0){
            nodeConnectionEntities = getRecentAliveNodes();
        }
        SignedData<NodeDto> signedData = signedNodeDtoProviderService.getWithCertificate();
        availabilityMessage.getMessage().setPasses(availabilityMessage.getMessage().getPasses() + 1);
        availabilityMessage.getMessage().setRoute(signedData);
        AvailabilityPublishEvent availabilityPublishEvent = new AvailabilityPublishEvent(this);
        availabilityPublishEvent.setAvailabilityMessage(availabilityMessage);
        availabilityPublishEvent.setNodesToContact(nodeConnectionEntities);
        applicationEventPublisher.publishEvent(availabilityPublishEvent);
        return true;
    }

    private List<NodeConnectionEntity> getRecentAliveNodes() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR_OF_DAY, -5);
        Date date = calendar.getTime();
        return nodeConnectionRepository.findAllByNodeTypeAndLastUpdateBefore(NodeType.PROXY, date);
    }
}
