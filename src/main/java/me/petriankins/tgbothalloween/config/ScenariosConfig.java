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
}