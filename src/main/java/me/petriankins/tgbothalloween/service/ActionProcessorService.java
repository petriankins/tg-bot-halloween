package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.petriankins.tgbothalloween.config.ScenariosConfig;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.ActionOption;
import me.petriankins.tgbothalloween.model.GameState;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static me.petriankins.tgbothalloween.constants.ConfigConstants.EMPTY_LINE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionProcessorService {

    private final ScenarioService scenarioService;
    private final KeyboardService keyboardService;
    private final ConfigService configService;
    private final ResourcesService resourcesService;
    private final GameResultService gameResultService;
    private final TelegramMessageService telegramMessageService;
    private final GameService gameService;

    public void processAction(CallbackQuery callbackQuery, GameState state) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();

        String[] parts = data.split("_");
        long scenarioId = Long.parseLong(parts[1]);
        int actionIndex = Integer.parseInt(parts[3]);

        if (state.currentScenario == null || scenarioId != state.currentScenario.id()) {
            telegramMessageService.sendTextMessage(chatId,
                    configService.getMessages().get(ConfigConstants.OLD_BUTTON));
            return;
        }

        ActionOption chosenAction = state.currentScenario.actions()[actionIndex];

        if (!validateAction(chatId, state, chosenAction)) {
            return;
        }

        log.info("USER CHOICE by @{}: {}",
                state.username != null ? state.username : "unknown",
                chosenAction.label());

        int oldResource1 = state.resource1;
        int oldResource2 = state.resource2;

        applyResourceChanges(state, chosenAction);

        String resourcesLine = resourcesService.getCurrentResourcesLine(state);
        String inventoryLine = resourcesService.getCurrentInventoryLine(state); // Уже отформатирован
        String newItemLine = "";

        if (chosenAction.givesItem() != null) {
            state.inventory.add(chosenAction.givesItem());
            String itemName = configService.getItemName(chosenAction.givesItem());
            newItemLine = configService.formatMessage("itemAcquired", "itemName", itemName);

            inventoryLine = resourcesService.getCurrentInventoryLine(state);
        }

        String resultText = chosenAction.resultText();
        String resourceChangesLine = buildResourceChangesLine(oldResource1, state.resource1, oldResource2, state.resource2);

        String combinedResultText = resourcesLine +
                inventoryLine +
                (newItemLine.isEmpty() ? "" : EMPTY_LINE + newItemLine) +
                EMPTY_LINE +
                resultText +
                (resourceChangesLine.isEmpty() ? "" : EMPTY_LINE + resourceChangesLine);

        if (isGameOver(chatId, messageId, state)) {
            return;
        }

        processNextScenario(chatId, messageId, state, chosenAction, combinedResultText);
    }

    private boolean validateAction(Long chatId, GameState state, ActionOption action) {
        if (action.requiredItem() != null && !state.inventory.contains(action.requiredItem())) {
            String itemName = configService.getItemName(action.requiredItem());
            String message = configService.formatMessage("actionRequiresItem", "itemName", itemName);
            telegramMessageService.sendTextMessage(chatId, message);
            return false;
        }

        ScenariosConfig.ActionRequirement requirement = action.requires();
        if (requirement != null) {
            int currentValue = requirement.getResource().equals(ConfigConstants.RESOURCE_1) ?
                    state.resource1 : state.resource2;
            if (currentValue <= requirement.getGreaterThan()) {
                String messageKey = requirement.getResource().equals(ConfigConstants.RESOURCE_1) ?
                        "requirementNotMetResource1" : "requirementNotMetResource2";
                telegramMessageService.sendTextMessage(chatId,
                        configService.getMessages().get(messageKey));
                return false;
            }
        }

        return true;
    }

    private void applyResourceChanges(GameState state, ActionOption action) {
        state.resource1 += action.resource1change();
        state.resource2 += action.resource2change();

        if (state.resource1 > 100) state.resource1 = 100;
        if (state.resource2 > 100) state.resource2 = 100;
    }

    private String buildResourceChangesLine(int oldR1, int newR1, int oldR2, int newR2) {
        StringBuilder changes = new StringBuilder();
        int diffR1 = newR1 - oldR1;
        int diffR2 = newR2 - oldR2;

        if (diffR1 != 0) {
            String r1Emoji = configService.getResources().get(ConfigConstants.RESOURCE_1).getEmoji();
            changes.append(String.format("%s %s%d", r1Emoji, diffR1 > 0 ? "+" : "", diffR1));
        }

        if (diffR2 != 0) {
            if (changes.length() > 0) {
                changes.append(", ");
            }
            String r2Emoji = configService.getResources().get(ConfigConstants.RESOURCE_2).getEmoji();
            changes.append(String.format("%s %s%d", r2Emoji, diffR2 > 0 ? "+" : "", diffR2));
        }

        return changes.toString();
    }

    private boolean isGameOver(Long chatId, Integer messageId, GameState state) {
        if (state.resource1 <= 0 || state.resource2 <= 0) {
            long endingId = 999L; // predefined ending ID for resource depletion fixme
            gameResultService.saveGameResult(state, endingId);

            String resourcesLine = resourcesService.getCurrentResourcesLine(state);
            String inventoryLine = resourcesService.getCurrentInventoryLine(state);
            String gameOverText = configService.formatMessage(ConfigConstants.GAME_OVER);

            String gameOverMessage = resourcesLine +
                    inventoryLine +
                    EMPTY_LINE +
                    gameOverText;

            telegramMessageService.editMessageCaption(chatId, messageId, gameOverMessage, null);
            gameService.endGame(chatId);
            return true;
        }
        return false;
    }

    private void processNextScenario(Long chatId, Integer messageId, GameState state,
                                     ActionOption action, String resultText) {
        Long nextScenarioId = action.nextScenarioId();

        Scenario nextScenario = scenarioService.getScenarioById(nextScenarioId);
        if (nextScenario == null) {
            log.error("Scenario not found by ID: {}", nextScenarioId);
            gameResultService.saveGameResult(state, -1L);
            telegramMessageService.sendTextMessage(chatId,
                    "История на этом заканчивается... (Ошибка сценария)");
            gameService.endGame(chatId);
            return;
        }

        if (!canAccessScenario(chatId, messageId, state, nextScenario, resultText)) {
            return;
        }

        showNextScenario(chatId, messageId, state, nextScenario, resultText);
    }

    private boolean canAccessScenario(Long chatId, Integer messageId, GameState state,
                                      Scenario nextScenario, String resultText) {
        if (nextScenario.requiredItem() != null &&
                !state.inventory.contains(nextScenario.requiredItem())) {
            String itemName = configService.getItemName(nextScenario.requiredItem());
            String pathBlockedMessage = configService.formatMessage("pathBlocked", "itemName", itemName);
            String combinedCaption = "%s%s%s".formatted(resultText, EMPTY_LINE, pathBlockedMessage);

            InlineKeyboardMarkup currentMarkup = keyboardService.createScenarioKeyboard(
                    state.currentScenario.id(),
                    state.currentScenario.actions(),
                    state
            );
            telegramMessageService.editMessageCaption(chatId, messageId, combinedCaption, currentMarkup);
            return false;
        }
        return true;
    }

    private void showNextScenario(Long chatId, Integer messageId, GameState state,
                                  Scenario nextScenario, String resultText) {
        state.currentScenario = nextScenario;
        state.currentScenarioId = nextScenario.id();

        String combinedCaption = "%s%s%s".formatted(resultText, EMPTY_LINE, nextScenario.description());

        if (nextScenario.actions() == null || nextScenario.actions().length == 0) {
            long endingId = nextScenario.id();
            gameResultService.saveGameResult(state, endingId);

            String picPath = "%d.jpg".formatted(nextScenario.id());
            telegramMessageService.editMessageMedia(chatId, messageId, picPath, combinedCaption, null);
            gameService.endGame(chatId);
            return;
        }

        InlineKeyboardMarkup markup = keyboardService.createScenarioKeyboard(
                nextScenario.id(),
                nextScenario.actions(),
                state
        );
        String picPath = "%d.jpg".formatted(nextScenario.id());
        telegramMessageService.editMessageMedia(chatId, messageId, picPath, combinedCaption, markup);
    }
}

