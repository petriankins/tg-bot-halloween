package me.petriankins.tgbothalloween;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.service.BotService;
import me.petriankins.tgbothalloween.service.CallbackHandlerService;
import me.petriankins.tgbothalloween.service.MessageHandlerService;
import me.petriankins.tgbothalloween.service.TelegramMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleTextGameBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotService botService;
    private final MessageHandlerService messageHandlerService;
    private final CallbackHandlerService callbackHandlerService;
    private final TelegramMessageService telegramMessageService;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        telegramMessageService.setTelegramClient(telegramClient);
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
            messageHandlerService.handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            callbackHandlerService.handleCallback(update.getCallbackQuery());
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
