package com.github.ep2p.eleuth.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityOutput {
    private boolean failed;
    @Builder.Default
    private List<String> errorMessages = new ArrayList<>();
}
