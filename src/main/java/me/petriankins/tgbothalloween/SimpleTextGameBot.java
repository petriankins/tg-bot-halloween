package me.petriankins.tgbothalloween;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.config.ScenariosConfig;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.ActionOption;
import me.petriankins.tgbothalloween.model.GameState;
import me.petriankins.tgbothalloween.model.Scenario;
import me.petriankins.tgbothalloween.service.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

        if (state.username == null && callbackQuery.getFrom() != null) {
            state.username = callbackQuery.getFrom().getUserName();
        }

        if (data.startsWith("ACTION_")) {
            String[] parts = data.split("_");
            long scenarioId = Long.parseLong(parts[1]);
            int actionIndex = Integer.parseInt(parts[3]);
            processAction(chatId, state, scenarioId, actionIndex);
        }
    }

    private void startGame(Long chatId) {
        gameService.startGame(chatId);

        String welcomeMessage = configService.getMessages().get(ConfigConstants.WELCOME) + "\n\n" +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_1, configService.getInitialResources().get(ConfigConstants.RESOURCE_1)) + "\n" +
                configService.getResourceDisplay(ConfigConstants.RESOURCE_2, configService.getInitialResources().get(ConfigConstants.RESOURCE_2));

        sendTextMessage(chatId, welcomeMessage);
        sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.LINE_BREAK));

        showScenarioById(chatId, 1L); // Start game from scenario with ID 1
    }

    private void showScenarioById(Long chatId, long scenarioId) {
        GameState state = gameService.getGameState(chatId);
        if (state == null) return;

        Scenario scenario = scenarioService.getScenarioById(scenarioId);

        if (scenario == null) {
            endGame(chatId, configService.formatMessage("gameWinWithPromo",
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2));
            return;
        }

        state.currentScenario = scenario;
        state.currentScenarioId = scenario.id();

        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                scenario.id(),
                scenario.actions()
        );

        String picPath = scenario.id() + ".png";
        sendPhotoWithKeyboard(chatId, picPath, scenario.description(), markup);
    }

    private void processAction(Long chatId, GameState state, long scenarioId, int actionIndex) {
        if (state.currentScenario == null || scenarioId != state.currentScenario.id()) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.OLD_BUTTON));
            return;
        }

        Scenario scenario = state.currentScenario;
        ActionOption[] actions = scenario.actions();

        if (actionIndex < 0 || actionIndex >= actions.length) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.INVALID_ACTION));
            return;
        }

        ActionOption chosenAction = actions[actionIndex];

        if (chosenAction.requiredItem() != null
                && !state.inventory.contains(chosenAction.requiredItem())) {
            sendTextMessage(chatId, configService.getMessages().get("actionRequiresItem"));
            return;
        }

        ScenariosConfig.ActionRequirement requirement = chosenAction.requires();
        if (requirement != null) {
            int currentResourceValue = requirement.getResource().equals(ConfigConstants.RESOURCE_1) ? state.resource1 : state.resource2;
            if (currentResourceValue <= requirement.getGreaterThan()) {
                String messageKey = requirement.getResource().equals(ConfigConstants.RESOURCE_1) ? "requirementNotMetResource1" : "requirementNotMetResource2";
                sendTextMessage(chatId, configService.getMessages().get(messageKey));
                return;
            }
        }

        log.info("USER CHOICE by @{}: {}",
                state.username != null ? state.username : "unknown",
                chosenAction.label());

        state.resource1 += chosenAction.resource1change();
        state.resource2 += chosenAction.resource2change();

        if (state.resource1 > 100) state.resource1 = 100;
        if (state.resource2 > 100) state.resource2 = 100;

        String resultText = chosenAction.resultText();

        if (chosenAction.givesItem() != null) {
            state.inventory.add(chosenAction.givesItem());
            resultText += configService.formatMessage("itemAcquired", "itemName", chosenAction.givesItem());
        }

        String finalText = resultText +
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

        Long nextScenarioId = chosenAction.nextScenarioId();

        if (nextScenarioId == null) {
            endGame(chatId, configService.formatMessage("gameWinWithPromo",
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2));
        } else {
            Scenario nextScenario = scenarioService.getScenarioById(nextScenarioId);
            if (nextScenario != null
                    && nextScenario.requiredItem() != null
                    && !state.inventory.contains(nextScenario.requiredItem())) {
                sendTextMessage(chatId, configService.getMessages().get("pathBlocked"));
                // staying on the current step
                return;
            }

            try {
                Thread.sleep(DELAY * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showScenarioById(chatId, nextScenarioId);
        }
    }

    private void endGame(Long chatId, String finalMessage) {
        sendTextMessage(chatId, finalMessage);
        gameService.endGame(chatId);
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
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
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send message with keyboard", e);
        }
    }

    private void sendPhotoWithKeyboard(Long chatId, String picPath, String caption, InlineKeyboardMarkup markup) {
        try {
            var resourceStream = getClass().getResourceAsStream("/pics/" + picPath);
            if (resourceStream == null) {
                log.error("Picture not found: {}", picPath);
                sendMessageWithKeyboard(chatId, caption, markup);
                return;
            }
            SendPhoto photo = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(resourceStream, picPath))
                    .caption(caption)
                    .replyMarkup(markup)
                    .parseMode(ParseMode.MARKDOWN)
                    .build();
            telegramClient.execute(photo);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo with keyboard", e);
            sendMessageWithKeyboard(chatId, caption, markup);
        }
    }
}

