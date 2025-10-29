package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.config.BotConfig;
import me.petriankins.tgbothalloween.config.ScenariosConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ScenariosConfig scenariosConfig;
    private final BotConfig botConfig;

    private final Random random = new Random();

    public String getScenarioKey() {
        return botConfig.getScenario();
    }

    public Map<String, String> getMessages() {
        return scenariosConfig.getMessages();
    }

    public Map<String, ScenariosConfig.ResourceConfig> getResources() {
        return scenariosConfig.getResources();
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

    public ScenariosConfig.ResourceChanges getResourceChanges() {
        return scenariosConfig.getResourceChanges();
    }

    public int getRandomPositive() {
        ScenariosConfig.ResourceChanges changes = getResourceChanges();
        return random.nextInt(changes.getPositiveMax() - changes.getPositiveMin() + 1) + changes.getPositiveMin();
    }

    public int getRandomNegative() {
        ScenariosConfig.ResourceChanges changes = getResourceChanges();
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
        ScenariosConfig.ResourceConfig resourceConfig = getResources().get(resourceType);
        if (resourceConfig == null) return "%s %d".formatted(resourceType, amount);

        String emoji = resourceConfig.getEmoji();
        String name = resourceConfig.getName();
        return "%s %s %d".formatted(emoji, name, amount);
    }
}