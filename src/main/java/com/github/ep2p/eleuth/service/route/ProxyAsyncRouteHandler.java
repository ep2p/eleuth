package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import com.github.ep2p.eleuth.util.Pipeline;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

//todo: ProxyAsyncRouteHandler & RingAsyncRouteHandler have a lot of similar behaviors. Move them to an abstract class
@Service
@Log4j2
public class ProxyAsyncRouteHandler implements AsyncRouteHandler {
    private final Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline;
    private final RowConnectionPool rowConnectionPool;
    private final MessageSignatureService messageSignatureService;


    public ProxyAsyncRouteHandler(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RowConnectionPool rowConnectionPool, MessageSignatureService messageSignatureService) {
        this.availabilityPipeline = availabilityPipeline;
        this.rowConnectionPool = rowConnectionPool;
        this.messageSignatureService = messageSignatureService;
    }

    @SneakyThrows
    @Override
    public void onAvailabilityMessage(AvailabilityMessage availabilityMessage) {
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        if(output.isFailed()){
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
    }

    private AvailabilityReply getAvailabilityResponse(AvailabilityMessage availabilityMessage, AvailabilityOutput output) {
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
