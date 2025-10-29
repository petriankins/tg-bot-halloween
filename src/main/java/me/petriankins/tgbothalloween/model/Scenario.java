package me.petriankins.tgbothalloween.model;

import lombok.Builder;

@Builder
public record Scenario(long id,
                       String description,
                       ActionOption[] actions,
                       String requiredItem,
                       Integer score) {
}