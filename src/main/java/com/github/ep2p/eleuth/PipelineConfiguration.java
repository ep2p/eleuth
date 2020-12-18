package com.github.ep2p.eleuth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.node.NodeInformation;
import com.github.ep2p.eleuth.repository.NodeConnectionRepository;
import com.github.ep2p.eleuth.service.EleuthKademliaRepositoryNode;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.RequestCacheService;
import com.github.ep2p.eleuth.service.SignedNodeDtoProviderService;
import com.github.ep2p.eleuth.service.stage.AvailabilityDistributionStage;
import com.github.ep2p.eleuth.service.stage.AvailabilityRequestCacheStage;
import com.github.ep2p.eleuth.service.stage.AvailabilityStoreStage;
import com.github.ep2p.eleuth.service.stage.AvailabilityVerificationStage;
import com.github.ep2p.eleuth.util.Pipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

@Configuration
public class PipelineConfiguration {
    private final NodeInformation nodeInformation;
    private final Validator validator;
    private final MessageSignatureService messageSignatureService;
    private final RequestCacheService requestCacheService;
    private final EleuthKademliaRepositoryNode kademliaNode;
    private final SignedNodeDtoProviderService signedNodeDtoProviderService;
    private final ObjectMapper objectMapper;
    private final NodeConnectionRepository nodeConnectionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public PipelineConfiguration(NodeInformation nodeInformation, Validator validator, MessageSignatureService messageSignatureService, RequestCacheService requestCacheService, EleuthKademliaRepositoryNode kademliaNode, SignedNodeDtoProviderService signedNodeDtoProviderService, NodeConnectionRepository nodeConnectionRepository, ObjectMapper objectMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.nodeInformation = nodeInformation;
        this.validator = validator;
        this.messageSignatureService = messageSignatureService;
        this.requestCacheService = requestCacheService;
        this.kademliaNode = kademliaNode;
        this.signedNodeDtoProviderService = signedNodeDtoProviderService;
        this.nodeConnectionRepository = nodeConnectionRepository;
        this.objectMapper = objectMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Bean("availabilityPipeline")
    public Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline(){
        Pipeline<AvailabilityMessage, AvailabilityOutput> pipeline = new Pipeline<>();
        pipeline.addStage(new AvailabilityVerificationStage(validator, messageSignatureService))
                .addStage(new AvailabilityRequestCacheStage(requestCacheService));

        if (nodeInformation.getNodeType().equals(NodeType.RING)) {
            pipeline.addStage(new AvailabilityStoreStage(kademliaNode, signedNodeDtoProviderService, objectMapper));
        }else {
            pipeline.addStage(new AvailabilityDistributionStage(nodeConnectionRepository, applicationEventPublisher, signedNodeDtoProviderService));
        }

        return pipeline;
    }

}
