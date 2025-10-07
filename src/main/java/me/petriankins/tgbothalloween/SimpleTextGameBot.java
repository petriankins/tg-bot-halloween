package me.petriankins.tgbothalloween;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.ActionOption;
import me.petriankins.tgbothalloween.model.GameState;
import me.petriankins.tgbothalloween.model.Scenario;
import me.petriankins.tgbothalloween.service.BotService;
import me.petriankins.tgbothalloween.service.ConfigService;
import me.petriankins.tgbothalloween.service.GameService;
import me.petriankins.tgbothalloween.service.KeyboardService;
import me.petriankins.tgbothalloween.service.ScenarioService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleTextGameBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    public static final int DELAY = 1;

    private final ScenarioService scenarioService;
    private final GameService gameService;
    private final KeyboardService keyboardService;
    private final ConfigService configService;
    private final BotService botService;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: {}", botSession.isRunning());
    }

    @Override
    public String getBotToken() {
        return botService.getBotToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        if ("/start".equalsIgnoreCase(text)) {
            startGame(chatId);
        } else {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.UNKNOWN_COMMAND));
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        GameState state = gameService.getGameState(chatId);
        if (state == null) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.USE_START));
            return;
        }

        // Store username in GameState
        if (state.username == null && callbackQuery.getFrom() != null) {
            state.username = callbackQuery.getFrom().getUserName();
        }

        if (data.startsWith("STEP_")) {
            String[] parts = data.split("_");
            int stepIndex = Integer.parseInt(parts[1]);
            int actionIndex = Integer.parseInt(parts[3]);
            processAction(chatId, state, stepIndex, actionIndex);
        }
    }

    private void startGame(Long chatId) {
        scenarioService.resetScenarios();
        gameService.startGame(chatId);

        String welcomeMessage = configService.getMessages().get(ConfigConstants.WELCOME) +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_1, configService.getInitialResources().get(ConfigConstants.RESOURCE_1)) + ", " +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_2, configService.getInitialResources().get(ConfigConstants.RESOURCE_2));

        sendTextMessage(chatId, welcomeMessage);
        sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.LINE_BREAK));

        showCurrentScenario(chatId);
    }

    private void showCurrentScenario(Long chatId) {
        GameState state = gameService.getGameState(chatId);
        if (state == null) return;

        if (!gameService.hasMoreScenarios(state)) {
            endGame(chatId, configService.getMessages().get(ConfigConstants.ALL_SCENARIOS_COMPLETE));
            return;
        }

        Scenario scenario = scenarioService.getNextScenario();
        if (scenario == null) {
            endGame(chatId, configService.getMessages().get(ConfigConstants.ALL_SCENARIOS_COMPLETE));
            return;
        }

        state.currentScenario = scenario;

        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                state.currentScenarioIndex,
                scenario.actions()
        );

        String picPath = scenario.id() + ".png";
        sendPhotoWithKeyboard(chatId, picPath, scenario.description(), markup);
    }

    private void processAction(Long chatId, GameState state, int stepIndex, int actionIndex) {
        if (stepIndex != state.currentScenarioIndex) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.OLD_BUTTON));
            return;
        }

        Scenario scenario = state.currentScenario;
        if (scenario == null) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.SCENARIO_NOT_FOUND));
            return;
        }

        ActionOption[] actions = scenario.actions();
        if (actionIndex < 0 || actionIndex >= actions.length) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.INVALID_ACTION));
            return;
        }

        ActionOption chosenAction = actions[actionIndex];

        log.info("USER CHOICE by @{}: {}",
                state.username != null ? state.username : "unknown",
                chosenAction.label());

        state.resource1 += chosenAction.resource1change();
        state.resource2 += chosenAction.resource2change();

        if (state.resource1 > 100) state.resource1 = 100;
        if (state.resource2 > 100) state.resource2 = 100;

        String finalText = chosenAction.resultText() +
                "\n\n\nТеперь у тебя:\n" +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_1, state.resource1) + "\n" +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_2, state.resource2);

        sendTextMessage(chatId, finalText);
        sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.LINE_BREAK));

        if (state.resource1 <= 0 || state.resource2 <= 0) {
            endGame(chatId, configService.formatMessage(ConfigConstants.GAME_OVER,
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2));
            return;
        }

        state.currentScenarioIndex++;

        if (state.currentScenarioIndex >= configService.getMaxScenariosPerGame()) {
            endGame(chatId, configService.formatMessage(ConfigConstants.GAME_COMPLETE,
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2));
        } else {
            // Add 5-second delay
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showCurrentScenario(chatId);
        }
    }

    private void endGame(Long chatId, String finalMessage) {
        Message message = null;
        try {
            message = telegramClient.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(finalMessage)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Failed to send end game message", e);
        }

        if (finalMessage.contains(configService.getMessages().get(ConfigConstants.GAME_COMPLETE))) {
            String username = message != null && message.getFrom() != null ?
                    message.getFrom().getUserName() : "unknown";
            log.info("GAME COMPLETED by @{}", username);
        }

        gameService.endGame(chatId);
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send text message", e);
        }
    }

    private void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(markup)
                .build();

        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send message with keyboard", e);
        }
    }

    private void sendPhotoWithKeyboard(Long chatId, String picPath, String caption, InlineKeyboardMarkup markup) {
        try {
            SendPhoto photo = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(getClass().getResourceAsStream("/pics/" + picPath), picPath))
                    .caption(caption)
                    .replyMarkup(markup)
                    .build();
            telegramClient.execute(photo);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo with keyboard", e);
            // Fallback to text message if image fails
            sendMessageWithKeyboard(chatId, caption, markup);
        }
    }


}
