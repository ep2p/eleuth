package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.kademlia.model.PingAnswer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class PingResponse extends BasicResponse {
    private PingAnswer<BigInteger> pingAnswer;

    public PingResponse(PingAnswer<BigInteger> pingAnswer) {
        this.pingAnswer = pingAnswer;
    }

    public PingResponse(SignedData<NodeDto> node, PingAnswer<BigInteger> pingAnswer) {
        super(node);
        this.pingAnswer = pingAnswer;
    }

    public PingResponse() {
    }
}
