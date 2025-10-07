package me.petriankins.tgbothalloween.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.model.Scenario;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ConfigService configService;
    private final ScenarioConstructor scenarioConstructor;
    private final Set<Integer> usedIndices = new HashSet<>();
    private final Random random = new Random();

    private List<Scenario> scenarioList;
    private int currentIndex = 0;

    @PostConstruct
    private void init() {
        scenarioList = scenarioConstructor.getScenarios();
    }

    public int getTotalScenariosCount() {
        return scenarioList.size();
    }

    public Scenario getNextScenario() {
        if (configService.isRandomScenarios()) {
            return getRandomScenario();
        }
        return getOrderedScenario();
    }

    private Scenario getRandomScenario() {
        if (usedIndices.size() >= scenarioList.size()) {
            return null;
        }

        while (true) {
            int index = random.nextInt(scenarioList.size());
            if (!usedIndices.contains(index)) {
                usedIndices.add(index);
                return scenarioList.get(index);
            }
        }
    }

    private Scenario getOrderedScenario() {
        if (currentIndex >= scenarioList.size()) {
            return null;
        }
        return scenarioList.get(currentIndex++);
    }

    public void resetScenarios() {
        usedIndices.clear();
        currentIndex = 0;
    }
}
