package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityReply;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.service.row.RowConnectionPool;
import lab.idioglossia.row.client.callback.ResponseCallback;
import lab.idioglossia.row.client.model.RowRequest;
import lab.idioglossia.row.client.model.RowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service("routeMessageSender")
public class RowRouteMessageSender implements RouteMessageSender<ROWConnectionInfo> {
    private final RowConnectionPool rowConnectionPool;

    public RowRouteMessageSender(RowConnectionPool rowConnectionPool) {
        this.rowConnectionPool = rowConnectionPool;
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

    //todo
    @Override
    public void sendAvailabilityReply(String nodeId, AvailabilityReply availabilityReply) {

    }
}
