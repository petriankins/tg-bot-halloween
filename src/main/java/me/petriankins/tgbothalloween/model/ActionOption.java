package me.petriankins.tgbothalloween.model;

import lombok.Builder;

@Builder
public record ActionOption(String label, String resultText, int resource1change, int resource2change, Long nextScenarioId) {
}
