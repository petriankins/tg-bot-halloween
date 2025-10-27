package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.model.GameState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackHandlerService {

    private final GameService gameService;
    private final ActionProcessorService actionProcessorService;
    private final TelegramMessageService telegramMessageService;
    private final ConfigService configService;

    public void handleCallback(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        User from = callbackQuery.getFrom();
        String data = callbackQuery.getData();

        GameState state = gameService.getGameState(chatId);
        if (state == null) {
            telegramMessageService.sendTextMessage(chatId,
                    configService.getMessages().get("useStart"));
            return;
        }

        updateUserInfo(state, from);

        if (data.startsWith("ACTION_")) {
            actionProcessorService.processAction(callbackQuery, state);
        }
    }

    private void updateUserInfo(GameState state, User from) {
        if (state.userId == null) {
            state.userId = from.getId();
        }
        if (state.username == null || !state.username.equals(from.getUserName())) {
            state.username = from.getUserName();
        }
    }
}
