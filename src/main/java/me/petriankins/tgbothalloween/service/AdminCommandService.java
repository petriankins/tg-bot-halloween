package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.db.GameCompletion;
import me.petriankins.tgbothalloween.db.GameCompletionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommandService {

    public static final int TOP_RECORDS = 50;

    private static final String LEADERBOARD_HEADER = "🏆 *Лидерборд* 🏆\n\n";
    private static final String NO_COMPLETIONS_MSG = "Пока никто не закончил игру.";
    private static final String STATS_ERROR_MSG = "Ошибка при получении статистики.";
    private static final String INVALID_FORMAT_MSG = "Неверный формат. Используй:\n`/sendmany <id1>,<id2>... Привет!`";
    private static final String REPORT_TEMPLATE = "Рассылка завершена.\nУспешно: %d\nОшибки: %d\n\nОтчет:\n%s";
    private static final Pattern COMMA_SPLIT = Pattern.compile(",");

    private static final String FAREWELL_INVALID_FORMAT_MSG = "Неверный формат. Используй:\n`/farewell\n<сообщение на новой строке>`";
    private static final String TEST_FAREWELL_INVALID_FORMAT_MSG = "Неверный формат. Используй:\n`/testfarewell\n<сообщение на новой строке>`";
    private static final String FAREWELL_REPORT_TEMPLATE = "Рассылка '%s' завершена.\nУспешно: %d\nОшибки: %d\n\nОтчет:\n%s";

    private final GameCompletionRepository gameCompletionRepository;
    private final TelegramMessageService telegramMessageService;

    public void handleStatsCommand(Long chatId) {
        log.info("Admin command /stats executed by chatId {}", chatId);
        try {
            Sort sort = Sort.by(
                    Sort.Order.desc("score"),
                    Sort.Order.asc("completedAt")
            );
            PageRequest pageable = PageRequest.of(0, TOP_RECORDS, sort);
            Page<GameCompletion> page = gameCompletionRepository.findAll(pageable);
            List<GameCompletion> leaderboard = page.getContent();

            if (leaderboard.isEmpty()) {
                telegramMessageService.sendTextMessage(chatId, NO_COMPLETIONS_MSG);
                return;
            }

            StringBuilder sb = new StringBuilder(LEADERBOARD_HEADER);
            int rank = 1;
            for (GameCompletion entry : leaderboard) {
                String username = entry.getUsername() != null ? entry.getUsername() : "???";
                String safeUsername = username.replace("_", "\\_");

                sb.append(String.format(
                        "%d. *%d очков* - @%s (ID: `%d`) - (❤️%d, 🧠%d) - K: %d%n",
                        rank++,
                        entry.getScore(),
                        safeUsername,
                        entry.getTelegramUserId(),
                        entry.getResource1(),
                        entry.getResource2(),
                        entry.getEndingId()
                ));
            }

            telegramMessageService.sendTextMessage(chatId, sb.toString());
        } catch (RuntimeException e) {
            log.error("Failed to execute /stats command", e);
            telegramMessageService.sendTextMessage(chatId, STATS_ERROR_MSG);
        }
    }

    public void handleSendCommand(Long chatId, String text) {
        log.info("Admin command /sendmany executed by chatId {}", chatId);
        if (text == null || text.isBlank()) {
            telegramMessageService.sendTextMessage(chatId, INVALID_FORMAT_MSG);
            return;
        }

        String[] parts = text.split(" ", 3);
        if (parts.length < 3) {
            telegramMessageService.sendTextMessage(chatId, INVALID_FORMAT_MSG);
            return;
        }

        String[] userIds = COMMA_SPLIT.split(parts[1]);
        String messageToSend = parts[2];

        if (userIds.length == 0 || messageToSend.isBlank()) {
            telegramMessageService.sendTextMessage(chatId, INVALID_FORMAT_MSG);
            return;
        }

        int successCount = 0;
        int failureCount = 0;
        StringBuilder report = new StringBuilder();

        for (String userIdStr : userIds) {
            String trimmed = userIdStr.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                Long targetUserId = Long.parseLong(trimmed);
                telegramMessageService.sendPlainTextMessage(targetUserId, messageToSend);
                report.append("✅ Сообщение отправлено: ").append(targetUserId).append('\n');
                successCount++;
            } catch (NumberFormatException ex) {
                log.warn("Invalid user ID format: {}", trimmed);
                report.append("❌ Неверный формат ID: ").append(trimmed).append('\n');
                failureCount++;
            } catch (RuntimeException ex) {
                log.warn("Failed to send message to user {}", trimmed, ex);
                report.append("❌ Ошибка отправки: ").append(trimmed).append('\n');
                failureCount++;
            }
        }

        telegramMessageService.sendTextMessage(
                chatId,
                String.format(REPORT_TEMPLATE, successCount, failureCount, report)
        );
    }

    public void handleFarewellCommand(Long adminChatId, String text, boolean isTest) {
        log.info("Admin command {} executed by chatId {}", isTest ? "/testfarewell" : "/farewell", adminChatId);

        int firstNewline = text.indexOf('\n');

        if (firstNewline == -1 || firstNewline + 1 >= text.length()) {
            String errorMsg = isTest ? TEST_FAREWELL_INVALID_FORMAT_MSG : FAREWELL_INVALID_FORMAT_MSG;
            telegramMessageService.sendTextMessage(adminChatId, errorMsg);
            return;
        }

        String messageToSend = text.substring(firstNewline + 1);

        Set<Long> uniqueUserIds;
        if (isTest) {
            uniqueUserIds = Set.of(adminChatId);
            telegramMessageService.sendTextMessage(adminChatId,
                    "ТЕСТ: Отправляю прощальное сообщение самому себе...\n\n---\n" + messageToSend + "\n---");
        } else {
            uniqueUserIds = gameCompletionRepository.findDistinctTelegramUserIds();
            telegramMessageService.sendTextMessage(adminChatId,
                    "ВНИМАНИЕ: Начинаю рассылку прощального сообщения по %d уникальным пользователям...".formatted(uniqueUserIds.size()));
        }

        // sending messages
        int successCount = 0;
        int failureCount = 0;
        StringBuilder report = new StringBuilder();

        for (Long targetUserId : uniqueUserIds) {
            try {
                telegramMessageService.sendPlainTextMessage(targetUserId, messageToSend);
                report.append("✅ Сообщение отправлено: ").append(targetUserId).append('\n');
                successCount++;
            } catch (RuntimeException ex) {
                log.warn("Failed to send farewell message to user {}", targetUserId, ex);
                report.append("❌ Ошибка отправки: ").append(targetUserId).append('\n');
                failureCount++;
            }
        }

        // send report to admin
        telegramMessageService.sendTextMessage(
                adminChatId,
                String.format(FAREWELL_REPORT_TEMPLATE, isTest ? "TEST FAREWELL" : "FAREWELL", successCount, failureCount, report)
        );
    }
}