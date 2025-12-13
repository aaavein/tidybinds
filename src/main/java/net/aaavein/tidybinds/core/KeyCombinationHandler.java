package net.aaavein.tidybinds.core;

import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.aaavein.tidybinds.util.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KeyCombinationHandler {

    private static final Map<KeyMapping, List<KeyMapping>> activeCombinations = new HashMap<>();

    private KeyCombinationHandler() {}

    public static void reload() {
        activeCombinations.clear();

        for (String entry : TidyBindsConfig.KEY_COMBINATIONS.get()) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) continue;

            KeyMapping trigger = KeyBindManager.getKeyByName(parts[0].trim());
            if (trigger == null) continue;

            List<KeyMapping> links = new ArrayList<>();
            for (String linkName : parts[1].split(",")) {
                KeyMapping link = KeyBindManager.getKeyByName(linkName.trim());
                if (link != null) {
                    links.add(link);
                }
            }

            if (!links.isEmpty()) {
                activeCombinations.put(trigger, links);
            }
        }
    }

    public static void tick() {
        if (activeCombinations.isEmpty()) return;

        for (Map.Entry<KeyMapping, List<KeyMapping>> entry : activeCombinations.entrySet()) {
            KeyMapping trigger = entry.getKey();
            List<KeyMapping> links = entry.getValue();

            boolean isTriggerDown = trigger.isDown();
            int triggerClicks = KeyMappingHelper.getClickCount(trigger);

            for (KeyMapping link : links) {
                if (link.isDown() != isTriggerDown) {
                    link.setDown(isTriggerDown);
                }

                if (triggerClicks > 0) {
                    int linkClicks = KeyMappingHelper.getClickCount(link);
                    if (linkClicks < triggerClicks) {
                        KeyMappingHelper.setClickCount(link, triggerClicks);
                    }
                }
            }
        }
    }
}