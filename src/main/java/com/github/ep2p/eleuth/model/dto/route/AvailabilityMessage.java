package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Builder
public class AvailabilityMessage extends ProtocolMessage<AvailabilityMessage.Message> {

    public AvailabilityMessage() {
        super(Type.AVAILABLE);
    }

    @Getter
    @Setter
    @Builder
    public static class Message implements Serializable {
        @NotNull
        private SignedData<AvailabilityMessageBody> body;
        private SignedData<NodeDto> route;
        @Min(0)
        private int passes;
    }

    @Builder
    @Getter
    @Setter
    public static class AvailabilityMessageBody implements Serializable {
        @NotNull
        private BigInteger nodeId;
        private String requestId;
        private String ringKey;
        private long timestamp;

        @AssertTrue
        public boolean isValidTimestamp(){
            long expiration = new Date().getTime() + (20 * 60 * 1000L);
            return timestamp < expiration;
        }
    }
}
