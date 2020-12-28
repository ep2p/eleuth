package com.github.ep2p.eleuth.model.dto.api;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseResponse {
    private Status status;

    public enum Status {
        SUCCESS, FAIL
    }
}
