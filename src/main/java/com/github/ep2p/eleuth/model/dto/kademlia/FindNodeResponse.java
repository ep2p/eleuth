package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import com.github.ep2p.kademlia.model.FindNodeAnswer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FindNodeResponse extends BasicResponse {
    private FindNodeAnswer<BigInteger, ROWConnectionInfo> answer;

    public FindNodeResponse(SignedData<NodeDto> node, SignedData<RingMemberProofDto> ringProof, FindNodeAnswer<BigInteger, ROWConnectionInfo> answer) {
        super(node, ringProof);
        this.answer = answer;
    }

    public FindNodeResponse(FindNodeAnswer<BigInteger, ROWConnectionInfo> answer) {
        this.answer = answer;
    }

    public FindNodeResponse() {
    }
}
