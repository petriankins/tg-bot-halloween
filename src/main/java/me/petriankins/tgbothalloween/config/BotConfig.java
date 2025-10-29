package me.petriankins.tgbothalloween.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
    private String username;
    private String token;
    private Long adminId;
    private String scenario = "default";
}
