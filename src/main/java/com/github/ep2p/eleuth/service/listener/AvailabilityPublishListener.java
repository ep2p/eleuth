package com.github.ep2p.eleuth.service.listener;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityResponse;
import com.github.ep2p.eleuth.model.event.AvailabilityPublishEvent;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import com.github.ep2p.eleuth.util.Pipeline;
import lab.idioglossia.row.client.RowClient;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

//listens to "AvailabilityPublishEvent" and forwards Availability message to other nodes
@Service
@Slf4j
public class AvailabilityPublishListener implements ApplicationListener<AvailabilityPublishEvent> {
    private final Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline;
    private final RowConnectionPool rowConnectionPool;

    @Autowired
    public AvailabilityPublishListener(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RowConnectionPool rowConnectionPool) {
        this.availabilityPipeline = availabilityPipeline;
        this.rowConnectionPool = rowConnectionPool;
    }

    public AvailabilityResponse available(AvailabilityMessage availabilityMessage){
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        AvailabilityResponse availabilityResponse = new AvailabilityResponse(output.isFailed() ? BaseResponse.Status.FAIL : BaseResponse.Status.SUCCESS);
        availabilityResponse.setErrors(output.getErrorMessages());
        return availabilityResponse;
    }

    @Override
    public void onApplicationEvent(AvailabilityPublishEvent availabilityPublishEvent) {
        ResponseCallback<AvailabilityResponse> callback = new ResponseCallback<AvailabilityResponse>(AvailabilityResponse.class) {
            @Override
            public void onResponse(RowResponse<AvailabilityResponse> rowResponse) {
                log.trace(rowResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("A node failed to handle availability request", throwable);
            }
        };
        availabilityPublishEvent.getNodesToContact().forEach(nodeConnectionEntity -> {
            RowClient client = rowConnectionPool.getClient(nodeConnectionEntity.getNodeId().toString(), nodeConnectionEntity);
            try {
                client.sendRequest(RowRequest.<AvailabilityMessage, Void>builder()
                        .body(availabilityPublishEvent.getAvailabilityMessage())
                        .address("") //todo: set address
                        .method(RowRequest.RowMethod.POST)
                        .build(), callback);
            } catch (IOException e) {
                log.error("Failed to send request", e);
            }
        });
    }
}
