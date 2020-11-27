package com.github.ep2p.eleuth.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
@EnableConfigurationProperties({ConfigProperties.class})
public class EleuthConfiguration {
    private final ConfigProperties configProperties;

    @Autowired
    public EleuthConfiguration(ConfigProperties configProperties) {
        this.configProperties = configProperties;
        log.info("Configuration: "+ configProperties.toString());
    }

}
