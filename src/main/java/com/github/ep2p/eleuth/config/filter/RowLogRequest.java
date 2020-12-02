package com.github.ep2p.eleuth.config.filter;

import lab.idioglossia.row.annotations.Filter;
import lab.idioglossia.row.domain.RowWebsocketSession;
import lab.idioglossia.row.domain.protocol.RequestDto;
import lab.idioglossia.row.domain.protocol.ResponseDto;
import lab.idioglossia.row.filter.RowFilter;
import lombok.extern.slf4j.Slf4j;

@Filter(type = Filter.Type.AFTER)
@Slf4j
public class RowLogRequest implements RowFilter {

    @Override
    public boolean filter(RequestDto requestDto, ResponseDto responseDto, RowWebsocketSession rowWebsocketSession) throws Exception {
        log.debug("=== LOG START ===");
        log.debug("Request: " + requestDto.toString());
        log.debug("Response: " + responseDto.toString());
        log.debug("=== LOG END ===");
        return true;
    }
}
