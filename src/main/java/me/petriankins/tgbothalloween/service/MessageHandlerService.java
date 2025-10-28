package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerService {

    private final GameFlowService gameFlowService;
    private final AdminCommandService adminCommandService;
    private final TelegramMessageService telegramMessageService;
    private final ConfigService configService;
    private final BotService botService;

    public void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User from = message.getFrom();

        if (isAdmin(from.getId())) {
            if ("/stats".equalsIgnoreCase(text)) {
                adminCommandService.handleStatsCommand(chatId);
                return;
            }
            if (text.startsWith("/sendmany")) {
                adminCommandService.handleSendCommand(chatId, text);
                return;
            }
        }

        if ("/start".equalsIgnoreCase(text)) {
            gameFlowService.startGame(chatId, from);
        } else {
            telegramMessageService.sendTextMessage(chatId,
                    configService.getMessages().get(ConfigConstants.UNKNOWN_COMMAND));
        }
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        Long adminId = botService.getAdminId();
        return adminId != null
                && adminId.equals(userId);
    }
}
