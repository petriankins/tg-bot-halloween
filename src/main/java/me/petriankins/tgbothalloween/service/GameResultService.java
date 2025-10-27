package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.db.GameCompletion;
import me.petriankins.tgbothalloween.db.GameCompletionRepository;
import me.petriankins.tgbothalloween.model.GameState;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameResultService {

    private final GameCompletionRepository gameCompletionRepository;

    public void saveGameResult(GameState state, long endingId) {
        if (state.userId == null) {
            log.error("Cannot save game result: userId is null. ChatId: {}", state.currentScenarioId);
            return;
        }

        try {
            int score = calculateScore(endingId, state);

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

    private int calculateScore(long endingId, GameState state) {
        int baseScore = switch ((int) endingId) {
            case 200 -> 100;  // Истинная хорошая концовка
            case 201 -> 150;  // Концовка силы (Особая)
            case 100, 102 -> 50;  // Нейтральная
            case 101 -> 10;  // Плохая
            default -> 0;  // Смерть или плохая концовка
        };

        int r1 = Math.max(0, state.resource1);
        int r2 = Math.max(0, state.resource2);
        return baseScore + r1 + r2;
    }
}
