package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.config.GameConfig;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.ActionOption;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioConstructor {
    private final ConfigService configService;

    public List<Scenario> getScenarios() {
        return configService.getScenarios().stream()
                .map(this::buildScenario)
                .collect(Collectors.toList());
    }

    private Scenario buildScenario(GameConfig.ScenarioConfig scenarioConfig) {
        long id = scenarioConfig.getId();
        String description = scenarioConfig.getDescription();
        List<GameConfig.ActionConfig> actionsData = scenarioConfig.getActions();

        ActionOption[] actions = actionsData.stream()
                .map(this::buildAction)
                .toArray(ActionOption[]::new);

        return Scenario.builder()
                .id(id)
                .description(description)
                .actions(actions)
                .build();
    }

    private ActionOption buildAction(GameConfig.ActionConfig actionConfig) {
        String label = actionConfig.getLabel();
        String resultText = actionConfig.getResultText();
        String resource1change = actionConfig.getResource1change();
        String resource2change = actionConfig.getResource2change();

        return ActionOption.builder()
                .label(label)
                .resultText(resultText)
                .resource1change(getResourceChange(resource1change))
                .resource2change(getResourceChange(resource2change))
                .build();
    }

    private int getResourceChange(String changeType) {
        return switch (changeType) {
            case ConfigConstants.POSITIVE -> configService.getRandomPositive();
            case ConfigConstants.NEGATIVE -> configService.getRandomNegative();
            default -> 0;
        };
    }
}
