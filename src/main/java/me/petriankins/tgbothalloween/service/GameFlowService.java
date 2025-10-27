package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.model.GameState;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameFlowService {

    private static final String LINE_BREAK = "\n\n";

    private final GameService gameService;
    private final ScenarioService scenarioService;
    private final KeyboardService keyboardService;
    private final ConfigService configService;
    private final ResourcesService resourcesService;
    private final TelegramMessageService telegramMessageService;

    public void startGame(Long chatId, User from) {
        gameService.startGame(chatId);
        GameState state = gameService.getGameState(chatId);

        state.userId = from.getId();
        state.username = from.getUserName();

        String welcomeMessage = resourcesService.getInitialResourcesLine()
                + LINE_BREAK
                + configService.getMessages().get("welcome");

        Scenario firstScenario = scenarioService.getScenarioById(1L);
        if (firstScenario == null) {
            log.error("Starting scenario with ID 1 not found!");
            telegramMessageService.sendTextMessage(chatId,
                    "Ошибка: стартовый сценарий не найден. Обратитесь к администратору.");
            return;
        }

        state.currentScenario = firstScenario;
        state.currentScenarioId = firstScenario.id();

        String combinedText = welcomeMessage + LINE_BREAK +
                configService.getMessages().get("lineBreak") + LINE_BREAK +
                firstScenario.description();

        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                firstScenario.id(),
                firstScenario.actions(),
                state
        );

        String picPath = firstScenario.id() + ".png";
        telegramMessageService.sendPhotoWithKeyboard(chatId, picPath, combinedText, markup);
    }

    public void endGame(Long chatId, String finalMessage) {
        telegramMessageService.sendTextMessage(chatId, finalMessage);
        gameService.endGame(chatId);
    }
}
