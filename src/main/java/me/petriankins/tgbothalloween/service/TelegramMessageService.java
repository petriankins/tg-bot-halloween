package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Setter
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {

    private final ConfigService configService;
    private TelegramClient telegramClient;

    public void sendTextMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send text message to chatId {}", chatId, e);
        }
    }

    public void sendPlainTextMessage(Long chatId, String text) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send plain text message to chatId {}", chatId, e);
        }
    }

    public void sendPhotoWithKeyboard(Long chatId, String picPath, String caption, InlineKeyboardMarkup markup) {
        String scenarioKey = configService.getScenarioKey();
        String fullPicPath = "/pics/" + scenarioKey + "/" + picPath;

        try {
            var resourceStream = getClass().getResourceAsStream(fullPicPath);
            if (resourceStream == null) {
                log.error("Picture not found: {}", fullPicPath);
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

    public void editMessageCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
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

    public void editMessageMedia(Long chatId, Integer messageId, String picPath, String caption, InlineKeyboardMarkup markup) {
        String scenarioKey = configService.getScenarioKey();
        String fullPicPath = "/pics/%s/%s".formatted(scenarioKey, picPath);

        var resourceStream = getClass().getResourceAsStream(fullPicPath);

        if (resourceStream == null) {
            // Picture not found. We MUST NOT try to edit media.
            // We must edit the text/caption of the existing message.
            log.warn("Picture not found for edit: {}. Trying to edit text/caption.", fullPicPath);
            tryEditTextOrCaption(chatId, messageId, caption, markup); // Use helper
            return;
        }

        try {
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
            // This might fail if the message is the same, or for other reasons.
            // Fallback to editing text/caption just in case.
            log.error("Failed to edit message media (media could not be changed). Falling back to text/caption edit. Error: {}", e.getMessage());
            tryEditTextOrCaption(chatId, messageId, caption, markup);
        }
    }

    /**
     * Tries to edit a message, first as text, then as a caption.
     * This handles cases where the original message might be text-only
     * (if sendPhoto failed) or a photo (if sendPhoto succeeded).
     */
    private void tryEditTextOrCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
        try {
            // First, try to edit it as a text message
            EditMessageText editText = EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(caption)
                    .replyMarkup(markup)
                    .parseMode(ParseMode.MARKDOWN)
                    .build();
            telegramClient.execute(editText);
        } catch (TelegramApiException eText) {
            // If that fails (e.g., it was a photo message)
            if (eText.getMessage().contains("message is not modified")) {
                log.warn("Message not modified (text).");
                return; // Nothing to do
            }

            try {
                // Second, try to edit it as a caption
                EditMessageCaption editCaption = EditMessageCaption.builder()
                        .chatId(chatId.toString())
                        .messageId(messageId)
                        .caption(caption)
                        .replyMarkup(markup)
                        .parseMode(ParseMode.MARKDOWN)
                        .build();
                telegramClient.execute(editCaption);
            } catch (TelegramApiException eCaption) {
                // If this also fails, we log the *second* error.
                if (eCaption.getMessage().contains("message is not modified")) {
                    log.warn("Message not modified (caption).");
                    return; // Nothing to do
                }
                // Log both errors for debugging
                log.warn("Failed to edit as EITHER text OR caption. Text fail: '{}', Caption fail: '{}'", eText.getMessage(), eCaption.getMessage());
            }
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
}

