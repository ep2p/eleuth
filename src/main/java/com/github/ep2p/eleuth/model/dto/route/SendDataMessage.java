package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Builder
public class SendDataMessage extends ProtocolMessage<SendDataMessage.Message> {

    public SendDataMessage() {
        super(Type.DATA_MESSAGE);
    }

    @Getter
    @Setter
    @Builder
    public static class Message implements Serializable {
        private SignedData<MessageData> data;
        private int passes;
    }

    @Getter
    @Setter
    @Builder
    private static class MessageData implements Serializable {
        @NotNull
        private BigInteger nodeId;
        @NotNull
        private BigInteger receiver;
        @NotNull
        private BigInteger ringId;
        @NotNull
        private Long timestamp;
        @NotNull
        private String message;

        @AssertTrue
        public boolean isValidTimestamp(){
            long expiration = new Date().getTime() + (5 * 60 * 1000L);
            return timestamp < expiration;
        }
    }
}
