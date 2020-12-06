package com.github.ep2p.eleuth.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private Status status;

    public enum Status {
        SUCCESS, FAIL
    }
}
