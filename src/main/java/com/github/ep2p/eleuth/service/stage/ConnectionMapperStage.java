package com.github.ep2p.eleuth.service.stage;

import com.github.ep2p.eleuth.model.AvailabilityOutput;
import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import com.github.ep2p.eleuth.util.Pipeline;
import lab.idioglossia.row.server.context.RowContext;
import lab.idioglossia.row.server.context.RowContextHolder;

public class ConnectionMapperStage implements Pipeline.Stage<AvailabilityMessage, AvailabilityOutput> {


    @Override
    public boolean process(AvailabilityMessage availabilityMessage, AvailabilityOutput o) {
        RowContext context = RowContextHolder.getContext();
        if(context.isRowRequest()){

        }
        return true;
    }
}
