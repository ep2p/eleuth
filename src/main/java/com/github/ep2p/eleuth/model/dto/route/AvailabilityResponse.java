package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AvailabilityResponse extends ProtocolResponse<AvailabilityResponse.AvailabilityResponseMessage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AvailabilityResponseMessage implements Serializable {
        private SignedData<AvailabilityResponseBody> body;
        private RingMemberProofDto ringProof;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class AvailabilityResponseBody extends BaseResponse implements Serializable {
        private String requestId;

        public AvailabilityResponseBody(Status status, String requestId) {
            super(status);
            this.requestId = requestId;
        }

        public AvailabilityResponseBody(String requestId) {
            this.requestId = requestId;
        }

        private boolean hit;
        private List<String> errors;
    }

}
