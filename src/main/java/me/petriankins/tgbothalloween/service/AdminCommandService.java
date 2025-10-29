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

    public static final int TOP_RECORDS = 30;

    private final GameCompletionRepository gameCompletionRepository;
    private final TelegramMessageService telegramMessageService;

    public void handleStatsCommand(Long chatId) {
        log.info("Admin command /stats executed by chatId {}", chatId);
        try {
            PageRequest pageable = PageRequest.of(0, TOP_RECORDS, Sort.by(Sort.Direction.DESC, "score"));
            List<GameCompletion> leaderboard = gameCompletionRepository.findAllByOrderByScoreDesc(pageable);

            if (leaderboard.isEmpty()) {
                telegramMessageService.sendTextMessage(chatId, "Пока никто не закончил игру.");
                return;
            }

            StringBuilder sb = new StringBuilder("🏆 *Лидерборд* 🏆\n\n");
            int rank = 1;
            for (GameCompletion entry : leaderboard) {
                sb.append(String.format("%d. *%d очков* - @%s (ID: `%d`) - (❤️%d, 🧠%d) - K: %d\n",
                        rank++,
                        entry.getScore(),
                        entry.getUsername() != null ? entry.getUsername() : "???",
                        entry.getTelegramUserId(),
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

    public void handleSendCommand(Long chatId, String text) {
        log.info("Admin command /sendmany executed by chatId {}", chatId);
        String[] parts = text.split(" ", 3);

        if (parts.length < 3) {
            telegramMessageService.sendTextMessage(chatId,
                    "Неверный формат. Используй:\n`/sendmany <id1>,<id2>... Привет!`");
            return;
        }

        String idsString = parts[1];
        String messageToSend = parts[2];
        String[] userIds = idsString.split(",");

        if (userIds.length == 0 || messageToSend.isEmpty()) {
            telegramMessageService.sendTextMessage(chatId,
                    "Неверный формат. Используй:\n`/sendmany <id1>,<id2>... Привет!`");
            return;
        }

        int count = 0;
        int failed = 0;
        StringBuilder report = new StringBuilder();

        for (String userIdStr : userIds) {
            try {
                Long targetUserId = Long.parseLong(userIdStr.trim());
                telegramMessageService.sendTextMessage(targetUserId, messageToSend);
                report.append(String.format("✅ Сообщение отправлено: %d\n", targetUserId));
                count++;
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID format: {}", userIdStr);
                report.append(String.format("❌ Неверный формат ID: %s\n", userIdStr));
                failed++;
            } catch (Exception e) {
                log.warn("Failed to send message to user {}", userIdStr, e);
                report.append(String.format("❌ Ошибка отправки: %s\n", userIdStr));
                failed++;
            }
        }

        telegramMessageService.sendTextMessage(chatId,
                String.format("Рассылка завершена.\nУспешно: %d\nОшибки: %d\n\nОтчет:\n%s",
                        count, failed, report.toString()));
    }
}
