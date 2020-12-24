package com.github.ep2p.eleuth.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RingMemberProofDto implements Serializable {
    private static final long serialVersionUID = -4159904306256260916L;
    private String key;
    private int part;
}
