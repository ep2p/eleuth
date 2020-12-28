package com.github.ep2p.eleuth.service.listener;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.model.event.AvailabilityPublishEvent;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
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
    private final RowConnectionPool rowConnectionPool;

    @Autowired
    public AvailabilityPublishListener(RowConnectionPool rowConnectionPool) {
        this.rowConnectionPool = rowConnectionPool;
    }

    @Override
    public void onApplicationEvent(AvailabilityPublishEvent availabilityPublishEvent) {
        ResponseCallback<AvailabilityReply> callback = new ResponseCallback<AvailabilityReply>(AvailabilityReply.class) {
            @Override
            public void onResponse(RowResponse<AvailabilityReply> rowResponse) {
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
