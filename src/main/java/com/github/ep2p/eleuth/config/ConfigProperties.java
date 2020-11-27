package com.github.ep2p.eleuth.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "config")
public class ConfigProperties {
    private NodeType nodeType;
    private String workingDir;
}
