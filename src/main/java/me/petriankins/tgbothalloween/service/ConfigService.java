package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.config.GameConfig;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final GameConfig gameConfig;
    private final Random random = new Random();

    public GameConfig.GameSettings getGameSettings() {
        return gameConfig.getGameSettings();
    }

    public Map<String, String> getMessages() {
        return gameConfig.getMessages();
    }

    public Map<String, GameConfig.ResourceConfig> getResources() {
        return gameConfig.getResources();
    }

    public List<GameConfig.ScenarioConfig> getScenarios() {
        return gameConfig.getScenarios();
    }

    public Map<String, Integer> getInitialResources() {
        return getGameSettings().getInitialResources();
    }

    public int getMaxScenariosPerGame() {
        return getGameSettings().getMaxScenariosPerGame();
    }

    public GameConfig.ResourceChanges getResourceChanges() {
        return getGameSettings().getResourceChanges();
    }

    public int getRandomPositive() {
        GameConfig.ResourceChanges changes = getResourceChanges();
        return random.nextInt(changes.getPositiveMax() - changes.getPositiveMin() + 1) + changes.getPositiveMin();
    }

    public int getRandomNegative() {
        GameConfig.ResourceChanges changes = getResourceChanges();
        return -(random.nextInt(Math.abs(changes.getNegativeMin()) - Math.abs(changes.getNegativeMax()) + 1) + Math.abs(changes.getNegativeMax()));
    }

    public String formatMessage(String messageKey, Object... params) {
        String message = getMessages().get(messageKey);
        if (message == null) return messageKey;

        if (params.length > 0) {
            for (int i = 0; i < params.length; i += 2) {
                if (i + 1 < params.length) {
                    String placeholder = "{" + params[i] + "}";
                    String value = String.valueOf(params[i + 1]);
                    message = message.replace(placeholder, value);
                }
            }
        }
        return message;
    }

    public String getResourceDisplay(String resourceType, int amount) {
        GameConfig.ResourceConfig resourceConfig = getResources().get(resourceType);
        if (resourceConfig == null) return resourceType + " " + amount;

        String emoji = resourceConfig.getEmoji();
        String name = resourceConfig.getName();
        return emoji + " " + name + " " + amount;
    }

    public boolean isRandomScenarios() {
        return Boolean.parseBoolean(getMessages().getOrDefault(ConfigConstants.RANDOM_SCENARIOS, "true"));
    }
}
