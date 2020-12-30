package com.github.ep2p.eleuth.service.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.model.entity.memory.NodeSessionEntity;
import com.github.ep2p.eleuth.repository.memory.NodeSessionRepository;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lab.idioglossia.row.server.repository.RowSessionRegistry;
import lab.idioglossia.row.server.service.RawMessagePublisherService;
import lab.idioglossia.row.server.ws.RowServerWebsocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service("routeMessageSender")
public class RowRouteMessageSender implements RouteMessageSender<ROWConnectionInfo> {
    private final RowConnectionPool rowConnectionPool;
    private final NodeSessionRepository nodeSessionRepository;
    private final RowSessionRegistry rowSessionRegistry;
    private final RawMessagePublisherService rawMessagePublisherService;


    public RowRouteMessageSender(RowConnectionPool rowConnectionPool, NodeSessionRepository nodeSessionRepository, RowSessionRegistry rowSessionRegistry, RawMessagePublisherService rawMessagePublisherService) {
        this.rowConnectionPool = rowConnectionPool;
        this.nodeSessionRepository = nodeSessionRepository;
        this.rowSessionRegistry = rowSessionRegistry;
        this.rawMessagePublisherService = rawMessagePublisherService;
    }

    @Override
    public void sendAvailabilityMessage(String nodeId, ROWConnectionInfo connectionInfo, AvailabilityMessage availabilityMessage) throws IOException {
        rowConnectionPool.getClient(nodeId, connectionInfo)
                .sendRequest(
                        RowRequest.<AvailabilityMessage, Void>builder()
                                .method(RowRequest.RowMethod.POST)
                                .body(availabilityMessage)
                                .address("/route/request/availability")
                                .build(),
                        new ResponseCallback<BaseResponse>(BaseResponse.class) {
                        @Override
                        public void onResponse(RowResponse<BaseResponse> rowResponse) {
                            log.debug("availability request response: " + rowResponse.getBody().toString());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log.error("Failed to send message to " + connectionInfo);
                        }
                    });
    }

    @Override
    public void sendAvailabilityReply(String nodeId, ROWConnectionInfo connectionInfo, AvailabilityReply availabilityReply) throws IOException {
        rowConnectionPool.getClient(nodeId, connectionInfo)
                .sendRequest(
                        RowRequest.<AvailabilityReply, Void>builder()
                                .method(RowRequest.RowMethod.POST)
                                .body(availabilityReply)
                                .address("/route/reply/availability")
                                .build(),
                        new ResponseCallback<BaseResponse>(BaseResponse.class) {
                            @Override
                            public void onResponse(RowResponse<BaseResponse> rowResponse) {
                                log.debug("availability reply response: " + rowResponse.getBody().toString());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                log.error("Failed to send message to " + connectionInfo);
                            }
                        });
    }

    @Override
    public void sendAvailabilityReply(String nodeId, AvailabilityReply availabilityReply) {
        List<NodeSessionEntity> nodeSessionEntities = nodeSessionRepository.findAllByNodeId(nodeId);
        nodeSessionEntities.forEach(nodeSessionEntity -> {
            RowServerWebsocket<?> session = rowSessionRegistry.getSession(nodeSessionEntity.getUserId(), nodeSessionEntity.getSessionId());
            try {
                rawMessagePublisherService.publish(session, availabilityReply);
            } catch (IOException e) {
                log.error("Failed to send message to " + nodeId, e);
            }
        });
    }
}
