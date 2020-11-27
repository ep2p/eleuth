package com.github.ep2p.eleuth.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class EleuthConfiguration {
    private final String nodeType;

    public EleuthConfiguration(@Value("${config.nodeType}") String nodeType) {
        this.nodeType = nodeType;
    }

    @Bean
    public ConfigModel configModel(){
        log.debug("NodeType: " + nodeType);
        return ConfigModel.builder()
                .nodeType(NodeType.valueOf(nodeType))
                .build();
    }

}
