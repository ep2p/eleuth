package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

@Getter
@Setter
@ToString
public class FindNodeRequest extends BasicRequest {
    private BigInteger lookupId;

    public FindNodeRequest(SignedData<NodeDto> caller, SignedData<RingMemberProofDto> proof, BigInteger lookupId) {
        super(caller, proof);
        this.lookupId = lookupId;
    }

    public FindNodeRequest(BigInteger lookupId) {
        this.lookupId = lookupId;
    }

    public FindNodeRequest() {
    }
}
