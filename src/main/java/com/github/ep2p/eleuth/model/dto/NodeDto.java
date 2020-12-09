package com.github.ep2p.eleuth.model.dto;

import com.github.ep2p.eleuth.model.NodeType;
import com.github.ep2p.eleuth.service.row.ROWConnectionInfo;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDto implements Serializable {
    private static final long serialVersionUID = -3883150909228695481L;
    private BigInteger id;
    private ROWConnectionInfo connectionInfo;
    private NodeType type;
    private long timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDto nodeDto = (NodeDto) o;
        return Objects.equals(getId(), nodeDto.getId()) &&
                getType() == nodeDto.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }
}
