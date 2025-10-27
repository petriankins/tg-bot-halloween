package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.db.GameCompletion;
import me.petriankins.tgbothalloween.db.GameCompletionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommandService {

    private final GameCompletionRepository gameCompletionRepository;
    private final TelegramMessageService telegramMessageService;

    public void handleStatsCommand(Long chatId) {
        log.info("Admin command /stats executed by chatId {}", chatId);
        try {
            PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "score"));
            List<GameCompletion> leaderboard = gameCompletionRepository.findAllByOrderByScoreDesc(pageable);

            if (leaderboard.isEmpty()) {
                telegramMessageService.sendTextMessage(chatId, "Пока никто не закончил игру.");
                return;
            }

            StringBuilder sb = new StringBuilder("🏆 *Лидерборд (Топ-10)* 🏆\n\n");
            int rank = 1;
            for (GameCompletion entry : leaderboard) {
                sb.append(String.format("%d. *%d очков* - @%s (❤️%d, 🧠%d) - Концовка: %d\n",
                        rank++,
                        entry.getScore(),
                        entry.getUsername() != null ? entry.getUsername() : "id:" + entry.getTelegramUserId(),
                        entry.getResource1(),
                        entry.getResource2(),
                        entry.getEndingId()
                ));
            }

            telegramMessageService.sendTextMessage(chatId, sb.toString());

        } catch (Exception e) {
            log.error("Failed to execute /stats command", e);
            telegramMessageService.sendTextMessage(chatId, "Ошибка при получении статистики.");
        }
    }

    public void handleSendTop5Command(Long chatId, String text) {
        log.info("Admin command /sendtop5 executed by chatId {}", chatId);
        String messageToSend;
        try {
            messageToSend = text.substring("/sendtop5".length()).trim();
        } catch (Exception e) {
            messageToSend = null;
        }

        if (messageToSend == null || messageToSend.isEmpty()) {
            telegramMessageService.sendTextMessage(chatId,
                    "Неверный формат. Используй:\n`/sendtop5 Привет! Ты в топ-5!`");
            return;
        }

        try {
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "score"));
            List<GameCompletion> top5 = gameCompletionRepository.findAllByOrderByScoreDesc(pageable);

            if (top5.isEmpty()) {
                telegramMessageService.sendTextMessage(chatId,
                        "В лидерборде пока никого нет. Сообщение не отправлено.");
                return;
            }

            int count = 0;
            for (GameCompletion entry : top5) {
                try {
                    telegramMessageService.sendTextMessage(entry.getTelegramUserId(), messageToSend);
                    count++;
                } catch (Exception e) {
                    log.warn("Failed to send message to user {}", entry.getTelegramUserId(), e);
                }
            }

            telegramMessageService.sendTextMessage(chatId,
                    String.format("Сообщение успешно отправлено %d из %d игроков в топ-5.", count, top5.size()));

        } catch (Exception e) {
            log.error("Failed to execute /sendtop5 command", e);
            telegramMessageService.sendTextMessage(chatId, "Ошибка при отправке сообщений.");
        }
    }
}
