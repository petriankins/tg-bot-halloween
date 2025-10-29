package me.petriankins.tgbothalloween.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties
public class ScenariosConfig {
    private List<ScenarioConfig> scenarios;
    private Map<String, String> items;
    private Map<String, String> messages;
    private Map<String, ResourceConfig> resources;
    private ResourceChanges resourceChanges;

    @Data
    public static class ScenarioConfig {
        private long id;
        private String description;
        private List<ActionConfig> actions;
        private String requiredItem;
    }

    @Data
    public static class ActionConfig {
        private String label;
        private String resultText;
        private String resource1change;
        private String resource2change;
        private Long nextScenarioId;
        private String givesItem;
        private String requiredItem;
        private ActionRequirement requires;
    }

    @Data
    public static class ActionRequirement {
        private String resource;
        private int greaterThan;
    }

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