package com.github.ep2p.eleuth.model.entity;

import com.github.ep2p.eleuth.model.dto.NodeDto;
import com.github.ep2p.eleuth.model.dto.SignedData;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeInfoStoreValue {
    private List<SignedData<NodeDto>> routes;
}
