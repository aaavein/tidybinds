package net.aaavein.tidybinds.core;

import net.aaavein.tidybinds.config.ClientConfig;
import net.aaavein.tidybinds.util.KeyReflection;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputHandler {
    private static Map<KeyMapping, List<KeyMapping>> activeCombinations = null;

    public static void reloadCombinations() {
        activeCombinations = new HashMap<>();
        for (String s : ClientConfig.KEY_COMBINATIONS.get()) {
            String[] parts = s.split(";");
            if (parts.length != 2) continue;

            KeyMapping trigger = KeyBindsManager.getKeyByName(parts[0].trim());
            if (trigger == null) continue;

            List<KeyMapping> links = new ArrayList<>();
            for (String linkName : parts[1].split(",")) {
                KeyMapping link = KeyBindsManager.getKeyByName(linkName.trim());
                if (link != null) links.add(link);
            }
            if (!links.isEmpty()) activeCombinations.put(trigger, links);
        }
    }

    public static void handleTick() {
        if (activeCombinations == null) return; // not initialized yet

        for (Map.Entry<KeyMapping, List<KeyMapping>> entry : activeCombinations.entrySet()) {
            KeyMapping trigger = entry.getKey();
            List<KeyMapping> links = entry.getValue();

            boolean isTriggerDown = trigger.isDown();
            int triggerClicks = KeyReflection.getClickCount(trigger);

            for (KeyMapping link : links) {
                // sync held state
                if (link.isDown() != isTriggerDown) {
                    link.setDown(isTriggerDown);
                }

                // sync click counts
                if (triggerClicks > 0) {
                    int linkClicks = KeyReflection.getClickCount(link);
                    if (linkClicks < triggerClicks) {
                        KeyReflection.setClickCount(link, triggerClicks);
                    }
                }
            }
        }
    }
}