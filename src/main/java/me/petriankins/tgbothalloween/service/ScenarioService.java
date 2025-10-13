package me.petriankins.tgbothalloween.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioConstructor scenarioConstructor;
    private Map<Long, Scenario> scenarioMap;

    @PostConstruct
    private void init() {
        scenarioMap = scenarioConstructor.getScenarios().stream()
                .collect(Collectors.toMap(Scenario::id, Function.identity()));
    }

    public Scenario getScenarioById(long id) {
        return scenarioMap.get(id);
    }
}
