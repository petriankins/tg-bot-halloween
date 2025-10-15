package me.petriankins.tgbothalloween.model;

import lombok.Builder;
import me.petriankins.tgbothalloween.config.ScenariosConfig;

@Builder
public record ActionOption(String label,
                           String resultText,
                           int resource1change,
                           int resource2change,
                           Long nextScenarioId,
                           String givesItem,
                           String requiredItem,
                           ScenariosConfig.ActionRequirement requires) {
}

