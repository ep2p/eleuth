package com.github.ep2p.eleuth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ep2p.eleuth.util.Path;
import lab.idioglossia.jsonsloth.JsonSlothManager;
import lab.idioglossia.jsonsloth.JsonSlothStorage;
import lab.idioglossia.sloth.storage.SlothStorage;
import lab.idioglossia.sloth.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;

@Configuration
public class SlothConfiguration {
    private final ConfigProperties configProperties;

    @Autowired
    public SlothConfiguration(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean("slothStorage")
    public SlothStorage slothStorage() throws IOException {
        String slothPath = Path.combine(configProperties.getWorkingDir(), "sloth") + "/";
        return new SlothStorage(slothPath, 10, 10);
    }

    @Bean("jsonSlothStorage")
    @DependsOn("slothStorage")
    public JsonSlothStorage jsonSlothStorage(Storage slothStorage){
        return new JsonSlothStorage(slothStorage);
    }

    @Bean("jsonSlothManager")
    @DependsOn({"jsonSlothStorage", "objectMapper"})
    public JsonSlothManager jsonSlothManager(JsonSlothStorage jsonSlothStorage, ObjectMapper objectMapper){
        return new JsonSlothManager(jsonSlothStorage, objectMapper);
    }

}
