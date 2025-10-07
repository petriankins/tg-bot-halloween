package me.petriankins.tgbothalloween.service;

import me.petriankins.tgbothalloween.model.ActionOption;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {

    public InlineKeyboardMarkup createScenarioKeyboard(int scenarioIndex, ActionOption[] actions) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < actions.length; i++) {
            String callbackData = "STEP_" + scenarioIndex + "_ACTION_" + i;
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(actions[i].label())
                    .callbackData(callbackData)
                    .build();
            rows.add(new InlineKeyboardRow(button));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
