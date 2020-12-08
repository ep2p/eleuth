package com.github.ep2p.eleuth.model.dto.route;

import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.kademlia.NodeDto;
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
public class QueryMessage extends ProtocolMessage<QueryMessage.Message> {

    public QueryMessage() {
        super(Type.QUERY);
    }

    @Getter
    @Setter
    @Builder
    public static class Message implements Serializable {
        @NotNull
        private SignedData<QueryMessageBody> body;
        private SignedData<NodeDto> route;
        private int passes;
    }

    @Builder
    @Getter
    @Setter
    public static class QueryMessageBody implements Serializable {
        @NotNull
        private BigInteger nodeId;
        private String requestId;
        private BigInteger ringId;
        private long timestamp;

        @AssertTrue
        public boolean isValidTimestamp(){
            long expiration = new Date().getTime() + (20 * 60 * 1000L);
            return timestamp < expiration;
        }
    }
}
