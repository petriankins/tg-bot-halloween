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
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleTextGameBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    public static final String LINE_BREAK = "\n\n";

    private final ScenarioService scenarioService;
    private final GameService gameService;
    private final KeyboardService keyboardService;
    private final ConfigService configService;
    private final BotService botService;
    private final ResourcesService resourcesService;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: {}", botSession.isRunning());
        setBotMenu();
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
            processAction(callbackQuery, state);
        }
    }

    private void startGame(Long chatId) {
        gameService.startGame(chatId);
        GameState state = gameService.getGameState(chatId);

        String welcomeMessage = resourcesService.getInitialResourcesLine()
                + LINE_BREAK
                + configService.getMessages()
                .get(ConfigConstants.WELCOME);

        Scenario firstScenario = scenarioService.getScenarioById(1L);
        if (firstScenario == null) {
            log.error("Starting scenario with ID 1 not found!");
            sendTextMessage(chatId, "Ошибка: стартовый сценарий не найден. Обратитесь к администратору.");
            return;
        }

        state.currentScenario = firstScenario;
        state.currentScenarioId = firstScenario.id();

        String combinedText = welcomeMessage + LINE_BREAK +
                configService.getMessages().get(ConfigConstants.LINE_BREAK)
                + LINE_BREAK
                + firstScenario.description();

        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                firstScenario.id(),
                firstScenario.actions(),
                state
        );

        String picPath = firstScenario.id() + ".png";
        sendPhotoWithKeyboard(chatId, picPath, combinedText, markup);
    }

    private void processAction(CallbackQuery callbackQuery, GameState state) {
        Long chatId = callbackQuery.getMessage()
                .getChatId();
        Integer messageId = callbackQuery.getMessage()
                .getMessageId();
        String data = callbackQuery.getData();

        String[] parts = data.split("_");
        long scenarioId = Long.parseLong(parts[1]);
        int actionIndex = Integer.parseInt(parts[3]);

        if (state.currentScenario == null || scenarioId != state.currentScenario.id()) {
            sendTextMessage(chatId, configService.getMessages().get(ConfigConstants.OLD_BUTTON));
            return;
        }

        ActionOption chosenAction = state.currentScenario.actions()[actionIndex];

        if (chosenAction.requiredItem() != null
                && !state.inventory.contains(chosenAction.requiredItem())) {
            String requiredItemName = configService.getItemName(chosenAction.requiredItem());
            String message = configService.formatMessage("actionRequiresItem", "itemName", requiredItemName);
            sendTextMessage(chatId, message);
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
            String itemId = chosenAction.givesItem();
            state.inventory.add(itemId);
            String itemName = configService.getItemName(itemId);
            resultText += configService.formatMessage("itemAcquired", "itemName", itemName);
        }

        String resourcesLine = resourcesService.getCurrentResourcesLine(state);
        String inventoryLine = resourcesService.getCurrentInventoryLine(state);

        String finalText =
                resourcesLine
                        + inventoryLine
                        + LINE_BREAK
                        + resultText;

        if (state.resource1 <= 0 || state.resource2 <= 0) {
            String gameOverMessage = configService.formatMessage(ConfigConstants.GAME_OVER,
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2);

            gameOverMessage = inventoryLine + LINE_BREAK + gameOverMessage;
            editMessageCaption(chatId, messageId, gameOverMessage, null);
            gameService.endGame(chatId);
            return;
        }

        Long nextScenarioId = chosenAction.nextScenarioId();

        if (nextScenarioId == null) {
            String gameWinMessage = configService.formatMessage("gameWinWithPromo",
                    ConfigConstants.RESOURCE_1, state.resource1,
                    ConfigConstants.RESOURCE_2, state.resource2);
            gameWinMessage = inventoryLine + LINE_BREAK + gameWinMessage;
            editMessageCaption(chatId, messageId, gameWinMessage, null);
            gameService.endGame(chatId);
            return;
        }

        Scenario nextScenario = scenarioService.getScenarioById(nextScenarioId);
        if (nextScenario == null) {
            endGame(chatId, "История на этом заканчивается...");
            return;
        }

        if (nextScenario.requiredItem() != null && !state.inventory.contains(nextScenario.requiredItem())) {
            String requiredItemName = configService.getItemName(nextScenario.requiredItem());
            String pathBlockedMessage = configService.formatMessage("pathBlocked", "itemName", requiredItemName);

            String combinedCaption = finalText + LINE_BREAK + pathBlockedMessage;

            InlineKeyboardMarkup currentMarkup = keyboardService.createScenarioKeyboard(
                    state.currentScenario.id(),
                    state.currentScenario.actions(),
                    state
            );
            editMessageCaption(chatId, messageId, combinedCaption, currentMarkup);
            return;
        }

        state.currentScenario = nextScenario;
        state.currentScenarioId = nextScenario.id();

        String separator = configService.getMessages().get(ConfigConstants.LINE_BREAK);
        String combinedCaption = finalText + LINE_BREAK + separator + LINE_BREAK + nextScenario.description();

        showScenarioByEditing(chatId, messageId, nextScenario, combinedCaption, state);
    }

    private void showScenarioByEditing(Long chatId, Integer messageId, Scenario scenario, String caption, GameState state) {
        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                scenario.id(),
                scenario.actions(),
                state
        );
        String picPath = scenario.id() + ".png";
        editMessageMedia(chatId, messageId, picPath, caption, markup);
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

    private void editMessageCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
        EditMessageCaption editMessage = EditMessageCaption.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .caption(caption)
                .replyMarkup(markup)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message caption. It might be the same as the old one. Error: {}", e.getMessage());
        }
    }

    private void editMessageMedia(Long chatId, Integer messageId, String picPath, String caption, InlineKeyboardMarkup markup) {
        try {
            var resourceStream = getClass().getResourceAsStream("/pics/" + picPath);
            if (resourceStream == null) {
                log.warn("Picture not found for edit: {}. Editing caption instead.", picPath);
                editMessageCaption(chatId, messageId, caption, markup);
                return;
            }

            InputMediaPhoto media = new InputMediaPhoto(resourceStream, picPath);
            media.setCaption(caption);
            media.setParseMode(ParseMode.MARKDOWN);

            EditMessageMedia editMessage = EditMessageMedia.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .media(media)
                    .replyMarkup(markup)
                    .build();
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to edit message media. Falling back to caption edit.", e);
            editMessageCaption(chatId, messageId, caption, markup);
        }
    }


    private void setBotMenu() {
        BotCommand startCommand = BotCommand.builder()
                .command("start")
                .description("Начать игру заново")
                .build();

        SetMyCommands setMyCommands = SetMyCommands.builder()
                .commands(List.of(startCommand))
                .build();

        try {
            telegramClient.execute(setMyCommands);
            log.info("Bot menu updated with /start command.");
        } catch (TelegramApiException e) {
            log.error("Failed to set bot menu", e);
        }
    }
}

