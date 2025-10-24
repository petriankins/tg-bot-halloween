package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.config.BotConfig;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {
    private final BotConfig botConfig;

    public String getBotToken() {
        return botConfig.getToken();
    }

    public String getBotUsername() {
        return botConfig.getUsername();
    }

    public Long getAdminId() {
        return botConfig.getAdminId();
    }
}
