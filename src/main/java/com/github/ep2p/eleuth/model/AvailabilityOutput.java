package com.github.ep2p.eleuth.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class AvailabilityOutput {
    private boolean failed;
    @Builder.Default
    private List<String> errorMessages = new ArrayList<>();
}
