package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AvailabilityReply extends ProtocolResponse<AvailabilityReply.AvailabilityReplyMessage> {

    public AvailabilityReply() {
        super(ProtocolMessage.Type.AVAILABLE);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AvailabilityReplyMessage implements Serializable {
        private SignedData<AvailabilityReplyBody> body;
        private SignedData<RingMemberProofDto> ringProof;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class AvailabilityReplyBody extends BaseResponse implements Serializable {
        private String requestId;

        public AvailabilityReplyBody(Status status, String requestId) {
            super(status);
            this.requestId = requestId;
        }

        public AvailabilityReplyBody(String requestId) {
            this.requestId = requestId;
        }

        private boolean hit;
        private List<String> errors;
    }

}
