package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.GameState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {
    private final Map<Long, GameState> userStates = new HashMap<>();
    private final ScenarioService scenarioService;
    private final ConfigService configService;

    public void startGame(Long chatId) {
        GameState newState = new GameState();
        Map<String, Integer> initialResources = configService.getInitialResources();
        newState.resource1 = initialResources.get(ConfigConstants.RESOURCE_1);
        newState.resource2 = initialResources.get(ConfigConstants.RESOURCE_2);
        newState.currentScenarioIndex = 0;

        userStates.put(chatId, newState);
    }

    public GameState getGameState(Long chatId) {
        return userStates.get(chatId);
    }

    public void endGame(Long chatId) {
        userStates.remove(chatId);
    }

    public boolean hasMoreScenarios(GameState state) {
        return state.currentScenarioIndex < scenarioService.getTotalScenariosCount();
    }
}
