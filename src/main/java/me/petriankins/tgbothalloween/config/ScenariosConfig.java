package me.petriankins.tgbothalloween.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties
public class ScenariosConfig {
    private List<ScenarioConfig> scenarios;

    @Data
    public static class ScenarioConfig {
        private long id;
        private String description;
        private List<ActionConfig> actions;
    }

    @Data
    public static class ActionConfig {
        private String label;
        private String resultText;
        private String resource1change;
        private String resource2change;
        private Long nextScenarioId;
    }
}
