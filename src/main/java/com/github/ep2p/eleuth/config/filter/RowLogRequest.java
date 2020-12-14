package com.github.ep2p.eleuth.config.filter;

import lab.idioglossia.row.server.annotations.Filter;
import lab.idioglossia.row.server.domain.protocol.RequestDto;
import lab.idioglossia.row.server.domain.protocol.ResponseDto;
import lab.idioglossia.row.server.filter.RowFilter;
import lab.idioglossia.row.server.ws.RowServerWebsocket;
import lombok.extern.slf4j.Slf4j;

@Filter(type = Filter.Type.AFTER)
@Slf4j
public class RowLogRequest implements RowFilter {

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowServerWebsocket<?> rowServerWebsocket) throws Exception {
        log.debug("=== LOG START ===");
        log.debug("Request: " + requestDto.toString());
        log.debug("Response: " + responseDto.toString());
        log.debug("=== LOG END ===");
        return true;
    }
    
}
