package com.github.ep2p.eleuth.model.dto.kademlia;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.RingMemberProofDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicResponse {
    private SignedData<NodeDto> node;
    private SignedData<RingMemberProofDto> membershipProof;
}
