package me.petriankins.tgbothalloween.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties
public class ResourcesConfig {
    private Map<String, ResourceConfig> resources;
    private ResourceChanges resourceChanges;

    @Data
    public static class ResourceConfig {
        private String name;
        private String emoji;
        private int initialValue;
    }

    @Data
    public static class ResourceChanges {
        private int positiveMin;
        private int positiveMax;
        private int negativeMin;
        private int negativeMax;
    }
}
