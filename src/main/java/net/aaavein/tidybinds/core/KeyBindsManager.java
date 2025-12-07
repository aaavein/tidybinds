package net.aaavein.tidybinds.core;

import net.aaavein.tidybinds.TidyBinds;
import net.aaavein.tidybinds.config.ClientConfig;
import net.aaavein.tidybinds.util.KeyReflection;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.*;

public class KeyBindsManager {

    public static final String HIDDEN_CATEGORY = "key.categories.tidybinds.hidden";

    private static final Map<KeyMapping, String> vanillaCategories = new HashMap<>();
    private static boolean capturedVanillaState = false;

    public static KeyMapping getKeyByName(String name) {
        if (Minecraft.getInstance().options == null) return null;
        for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
            if (key.getName().equals(name)) return key;
        }
        return null;
    }

    public static void applyConfiguration() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        if (ClientConfig.LOG_ACTIONS.get()) TidyBinds.LOGGER.info("Applying TidyBinds configuration...");

        // snapshot vanilla state (only once)
        if (!capturedVanillaState) {
            for (KeyMapping key : mc.options.keyMappings) {
                vanillaCategories.put(key, key.getCategory());
            }
            capturedVanillaState = true;
        }

        // reset all keys to vanilla categories before applying new config
        for (Map.Entry<KeyMapping, String> entry : vanillaCategories.entrySet()) {
            KeyReflection.setCategory(entry.getKey(), entry.getValue());
        }

        // apply user moves
        applyCategoryMoves();

        // handle hidden keys logic
        if (ClientConfig.SHOW_HIDDEN_KEYS.get()) {
            moveHiddenKeysToDebugCategory(mc);
        }

        // reorder categories
        applyCategoryReordering();

        // ensure the static CATEGORIES set contains any new custom categories
        Set<String> knownCategories = KeyReflection.getCategoriesSet();
        if (knownCategories != null) {
            for (KeyMapping key : mc.options.keyMappings) {
                knownCategories.add(key.getCategory());
            }
        }

        if (ClientConfig.PRINT_KEYS.get()) printDebugKeys(mc);
        if (ClientConfig.PRINT_CATEGORIES.get()) printDebugCategories();
    }

    private static void applyCategoryMoves() {
        for (String entry : ClientConfig.KEY_CATEGORIES.get()) {
            String[] parts = entry.split(";");
            if (parts.length == 2) {
                KeyMapping key = getKeyByName(parts[0].trim());
                if (key != null) {
                    KeyReflection.setCategory(key, parts[1].trim());
                }
            }
        }
    }

    private static void applyCategoryReordering() {
        Map<String, Integer> sortOrder = KeyReflection.getSortOrderMap();
        Set<String> categories = KeyReflection.getCategoriesSet();

        if (sortOrder == null || categories == null) return;

        for (String entry : ClientConfig.CATEGORY_ORDER.get()) {
            String[] parts = entry.split(";");
            if (parts.length == 2) {
                try {
                    sortOrder.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {}
            }
        }

        categories.clear();
        categories.addAll(sortOrder.keySet());
    }

    private static void moveHiddenKeysToDebugCategory(Minecraft mc) {
        Set<String> keysToHide = new HashSet<>(ClientConfig.HIDE_KEYS.get());

        for (String combo : ClientConfig.KEY_COMBINATIONS.get()) {
            String[] parts = combo.split(";");
            if (parts.length == 2) {
                Collections.addAll(keysToHide, parts[1].split(","));
            }
        }

        // use the translation key constant
        String debugCategory = HIDDEN_CATEGORY;

        Set<String> cats = KeyReflection.getCategoriesSet();
        if (cats != null) cats.add(debugCategory);

        for (KeyMapping key : mc.options.keyMappings) {
            if (keysToHide.contains(key.getName())) {
                KeyReflection.setCategory(key, debugCategory);
                if (ClientConfig.LOG_ACTIONS.get()) TidyBinds.LOGGER.info("Moved hidden key {} to debug category", key.getName());
            }
        }
    }

    private static void printDebugKeys(Minecraft mc) {
        TidyBinds.LOGGER.info("--- Available Keys ---");
        Arrays.stream(mc.options.keyMappings).map(KeyMapping::getName).sorted().forEach(TidyBinds.LOGGER::info);
    }

    private static void printDebugCategories() {
        TidyBinds.LOGGER.info("--- Available Categories ---");
        Set<String> cats = KeyReflection.getCategoriesSet();
        if (cats != null) cats.stream().sorted().forEach(TidyBinds.LOGGER::info);
    }
}