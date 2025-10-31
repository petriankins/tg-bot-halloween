package me.petriankins.tgbothalloween.service;

import lombok.RequiredArgsConstructor;
import me.petriankins.tgbothalloween.constants.ConfigConstants;
import me.petriankins.tgbothalloween.model.GameState;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourcesService {

    public static final String RESOURCES_SEPARATOR = " | ";

    private final ConfigService configService;

    public String getInitialResourcesLine() {
        return configService.getResourceDisplay(ConfigConstants.RESOURCE_1, configService.getInitialResources().get(ConfigConstants.RESOURCE_1))
                + " | "
                + configService.getResourceDisplay(ConfigConstants.RESOURCE_2, configService.getInitialResources().get(ConfigConstants.RESOURCE_2));
    }

    public String getCurrentResourcesLine(GameState state) {
        return configService.getResourceDisplay(ConfigConstants.RESOURCE_1, state.resource1)
                + RESOURCES_SEPARATOR
                + configService.getResourceDisplay(ConfigConstants.RESOURCE_2, state.resource2);
    }

    public String getCurrentInventoryLine(GameState state) {
        if (state.inventory == null || state.inventory.isEmpty()) {
            return "";
        }

        String header = configService.formatMessage(ConfigConstants.INVENTORY_LINE);
        String itemsList = state.inventory.stream()
                .map(configService::getItemName)
                .collect(Collectors.joining("\n"));
        return header + "\n" + itemsList;
    }
}

