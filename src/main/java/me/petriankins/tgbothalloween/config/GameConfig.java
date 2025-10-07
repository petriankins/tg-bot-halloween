package me.petriankins.tgbothalloween.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameConfig {
    private GameSettings gameSettings;
    private Map<String, String> messages;
    private Map<String, ResourceConfig> resources;
    private List<ScenarioConfig> scenarios;

    @Data
    public static class GameSettings {
        private Map<String, Integer> initialResources;
        private int maxScenariosPerGame;
        private ResourceChanges resourceChanges;
    }

    @Data
    public static class ResourceChanges {
        private int positiveMin;
        private int positiveMax;
        private int negativeMin;
        private int negativeMax;
    }

    @Data
    public static class ResourceConfig {
        private String name;
        private String emoji;
    }

    @Data
    public static class ScenarioConfig {
        private int id;
        private String description;
        private List<ActionConfig> actions;
    }

    @Data
    public static class ActionConfig {
        private String label;
        private String resultText;
        private String resource1change;
        private String resource2change;
    }
}
