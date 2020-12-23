package com.github.ep2p.eleuth.model.dto.api;

import com.github.ep2p.eleuth.model.entity.RingMemberEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RingMemberResponse extends BaseResponse {
    private RingMemberEntity member;
    public RingMemberResponse() {
        super(Status.SUCCESS);
    }
}
