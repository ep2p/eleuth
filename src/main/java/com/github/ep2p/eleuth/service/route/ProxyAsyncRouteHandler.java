package com.github.ep2p.eleuth.service.route;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.service.MessageSignatureService;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.eleuth.util.Pipeline;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//todo: ProxyAsyncRouteHandler & RingAsyncRouteHandler have a lot of similar behaviors. Move them to an abstract class
@Service
@Slf4j
public class ProxyAsyncRouteHandler extends AbstractRouteHandler implements AsyncRouteHandler {
    private final Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline;
    private final RouteMessageSender<ROWConnectionInfo> routeMessageSender;


    public ProxyAsyncRouteHandler(Pipeline<AvailabilityMessage, AvailabilityOutput> availabilityPipeline, RouteMessageSender<ROWConnectionInfo> routeMessageSender, MessageSignatureService messageSignatureService) {
        super(messageSignatureService);
        this.availabilityPipeline = availabilityPipeline;
        this.routeMessageSender = routeMessageSender;
    }

    @SneakyThrows
    @Override
    public void onAvailabilityMessage(AvailabilityMessage availabilityMessage) {
        AvailabilityOutput output = new AvailabilityOutput();
        availabilityPipeline.run(availabilityMessage, output);
        if(output.isFailed()){
            SignedData<NodeDto> route = availabilityMessage.getMessage().getRoute();
            routeMessageSender.sendAvailabilityReply(route.getData().getId().toString(), route.getData().getConnectionInfo(), getAvailabilityResponse(availabilityMessage, output));
        }
    }


}
