package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.service.row.RowConnectionMapper;
import com.github.ep2p.eleuth.util.Pipeline;

public class AvailabilityConnectionMapperStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {
    private final RowConnectionMapper rowConnectionMapper;

    public AvailabilityConnectionMapperStage(RowConnectionMapper rowConnectionMapper) {
        this.rowConnectionMapper = rowConnectionMapper;
    }

    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        rowConnectionMapper.addMapping(availabilityMessage.getMessage().getBody().getData().getNodeId().toString());
        return true;
    }
}
