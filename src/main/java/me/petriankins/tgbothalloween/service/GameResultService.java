package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.db.GameCompletion;
import me.petriankins.tgbothalloween.db.GameCompletionRepository;
import me.petriankins.tgbothalloween.model.GameState;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameResultService {

    private final GameCompletionRepository gameCompletionRepository;
    private final ScenarioService scenarioService;

    public void saveGameResult(GameState state, long endingId) {
        if (state.userId == null) {
            log.error("Cannot save game result: userId is null. ChatId: {}", state.currentScenarioId);
            return;
        }

        try {
            Scenario endingScenario = scenarioService.getScenarioById(endingId);
            int score = calculateScore(endingScenario, state);

            GameCompletion completion = new GameCompletion();
            completion.setTelegramUserId(state.userId);
            completion.setUsername(state.username);
            completion.setScore(score);
            completion.setResource1(state.resource1);
            completion.setResource2(state.resource2);
            completion.setEndingId(endingId);

            gameCompletionRepository.save(completion);
            log.info("Game result saved for user @{} (ID: {}) with score {}",
                    state.username, state.userId, score);

        } catch (Exception e) {
            log.error("Failed to save game result for user ID {}", state.userId, e);
        }
    }

    private int calculateScore(Scenario endingScenario, GameState state) {
        int baseScore = 0;

        if (endingScenario != null && endingScenario.score() != null) {
            baseScore = endingScenario.score();
        } else {
            log.warn("Ending scenario ID {} does not have 'score' defined. Using baseScore: 0",
                    endingScenario != null ? endingScenario.id() : "UNKNOWN");
        }

        int r1 = Math.max(0, state.resource1);
        int r2 = Math.max(0, state.resource2);
        return baseScore + r1 + r2;
    }
}
