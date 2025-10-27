package me.petriankins.tgbothalloween.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Setter
@Slf4j
@Service
public class TelegramMessageService {

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

    public void sendPhotoWithKeyboard(Long chatId, String picPath, String caption, InlineKeyboardMarkup markup) {
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
