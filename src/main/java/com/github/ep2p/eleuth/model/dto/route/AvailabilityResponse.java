package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AvailabilityResponse extends BaseResponse {
    public List<String> errors;

    public AvailabilityResponse(Status status) {
        super(status);
    }
}
