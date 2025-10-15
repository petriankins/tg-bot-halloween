package me.petriankins.tgbothalloween.model;

import java.util.HashSet;
import java.util.Set;

public class GameState {
    public int resource1;
    public int resource2;
    public long currentScenarioId;
    public Scenario currentScenario;
    public String username;
    public Set<String> inventory = new HashSet<>();
}