package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.util.Path;
import lab.idioglossia.jsonsloth.JsonSlothManager;
import lab.idioglossia.jsonsloth.JsonSlothStorage;
import lab.idioglossia.sloth.SlothStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;

@Configuration
public class SlothDbConfiguration {
    private final ConfigProperties configProperties;

    @Autowired
    public SlothDbConfiguration(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public SlothStorage slothStorage() throws IOException {
        return new SlothStorage(Path.combine(configProperties.getWorkingDir(), "storage") + "/" , 10, 20);
    }

    @DependsOn("slothStorage")
    @Bean
    public JsonSlothStorage jsonSlothStorage(SlothStorage slothStorage){
        return new JsonSlothStorage(slothStorage);
    }

    @DependsOn({"jsonSlothStorage", "objectMapper"})
    @Bean
    public JsonSlothManager jsonSlothManager(JsonSlothStorage jsonSlothStorage, ObjectMapper objectMapper){
        return new JsonSlothManager(jsonSlothStorage, objectMapper);
    }
}
