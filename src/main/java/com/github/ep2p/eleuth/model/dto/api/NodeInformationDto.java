package com.github.ep2p.eleuth.model.dto.api;

import com.github.ep2p.eleuth.model.dto.SignedData;
import com.github.ep2p.eleuth.model.dto.NodeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeInformationDto {
    private SignedData<NodeInfo> info;

    @Getter
    @Setter
    public static class NodeInfo extends NodeDto {
        private static final long serialVersionUID = 4998040335298288219L;
        public static NodeInfo extendFrom(NodeDto nodeDto){
            NodeInfo data = new NodeInfo();
            data.setConnectionInfo(nodeDto.getConnectionInfo());
            data.setId(nodeDto.getId());
            data.setType(nodeDto.getType());
            return data;
        }
        private String certificate;
    }

}
