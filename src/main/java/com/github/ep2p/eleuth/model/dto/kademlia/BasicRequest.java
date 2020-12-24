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
@ToString
public class BasicRequest {
    private SignedData<NodeDto> caller;
    private SignedData<RingMemberProofDto> membershipProof;
}
