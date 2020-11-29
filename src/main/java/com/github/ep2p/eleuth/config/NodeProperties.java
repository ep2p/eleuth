package com.github.ep2p.eleuth.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "node")
public class NodeProperties {
    private String host;
    private int port;
    private boolean ssl;
}
