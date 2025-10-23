package me.petriankins.tgbothalloween.service;

import me.petriankins.tgbothalloween.model.ActionOption;
import me.petriankins.tgbothalloween.model.GameState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class KeyboardService {

    public InlineKeyboardMarkup createScenarioKeyboard(long scenarioId, ActionOption[] actions, GameState state) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        Set<String> inventory = state.inventory;

        for (int i = 0; i < actions.length; i++) {
            ActionOption action = actions[i];
            String requiredItem = action.requiredItem();

            if (requiredItem != null && !inventory.contains(requiredItem)) {
                continue; // we're skipping this action as the required item is not in inventory
            }

            String callbackData = "ACTION_" + scenarioId + "_OPTION_" + i;
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(action.label())
                    .callbackData(callbackData)
                    .build();
            rows.add(new InlineKeyboardRow(button));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
