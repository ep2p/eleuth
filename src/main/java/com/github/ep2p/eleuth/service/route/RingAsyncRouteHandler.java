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
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import com.github.ep2p.eleuth.util.Pipeline;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
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
    private final RowConnectionPool rowConnectionPool;
    private final SignedRingProofProvider signedRingProofProvider;
    private final MessageSignatureService messageSignatureService;

    public RingAsyncRouteHandler(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RowConnectionPool rowConnectionPool, SignedRingProofProvider signedRingProofProvider, MessageSignatureService messageSignatureService) {
        this.availabilityPipeline = availabilityPipeline;
        this.rowConnectionPool = rowConnectionPool;
        this.signedRingProofProvider = signedRingProofProvider;
        this.messageSignatureService = messageSignatureService;
    }

    @SneakyThrows
    @Override
    public void onAvailabilityMessage(AvailabilityMessage availabilityMessage) {
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        SignedData<NodeDto> route = availabilityMessage.getMessage().getRoute();
        RowClient client = rowConnectionPool.getClient(route.getData().getId().toString(), route.getData().getConnectionInfo());
        client.sendRequest(RowRequest.<AvailabilityReply, Void>builder()
                .method(RowRequest.RowMethod.POST)
                .address("") //todo
                .body(getAvailabilityResponse(availabilityMessage, output))
                .build(), new ResponseCallback<BaseResponse>(BaseResponse.class) {
            @Override
            public void onResponse(RowResponse<BaseResponse> rowResponse) {

            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Failed to send AvailabilityReply");
            }
        });
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
