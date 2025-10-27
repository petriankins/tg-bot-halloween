package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.config.GameConfig;
import me.petriankins.tgbothalloween.config.MessagesConfig;
import me.petriankins.tgbothalloween.config.ResourcesConfig;
import me.petriankins.tgbothalloween.config.ScenariosConfig;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final GameConfig gameConfig;
    private final ScenariosConfig scenariosConfig;
    private final MessagesConfig messagesConfig;
    private final ResourcesConfig resourcesConfig;
    private final Random random = new Random();

    public GameConfig.GameSettings getGameSettings() {
        return gameConfig.getGameSettings();
    }

    public Map<String, String> getMessages() {
        return messagesConfig.getMessages();
    }

    public Map<String, ResourcesConfig.ResourceConfig> getResources() {
        return resourcesConfig.getResources();
    }

    public List<ScenariosConfig.ScenarioConfig> getScenarios() {
        return scenariosConfig.getScenarios();
    }

    public Map<String, String> getItems() {
        return scenariosConfig.getItems();
    }

    public String getItemName(String itemId) {
        if (itemId == null) {
            return "Неизвестный предмет";
        }
        return getItems().getOrDefault(itemId, itemId);
    }

    public Map<String, Integer> getInitialResources() {
        return getResources().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getInitialValue()));
    }

    public int getMaxScenariosPerGame() { // fixme
        return getGameSettings().getMaxScenariosPerGame();
    }

    public ResourcesConfig.ResourceChanges getResourceChanges() {
        return resourcesConfig.getResourceChanges();
    }

    public int getRandomPositive() {
        ResourcesConfig.ResourceChanges changes = getResourceChanges();
        return random.nextInt(changes.getPositiveMax() - changes.getPositiveMin() + 1) + changes.getPositiveMin();
    }

    public int getRandomNegative() {
        ResourcesConfig.ResourceChanges changes = getResourceChanges();
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
        ResourcesConfig.ResourceConfig resourceConfig = getResources().get(resourceType);
        if (resourceConfig == null) return "%s %d".formatted(resourceType, amount);

        String emoji = resourceConfig.getEmoji();
        String name = resourceConfig.getName();
        return "%s %s %d".formatted(emoji, name, amount);
    }

    public boolean isRandomScenarios() { // fixme
        return Boolean.parseBoolean(getMessages().getOrDefault(ConfigConstants.RANDOM_SCENARIOS, "true"));
    }
}
