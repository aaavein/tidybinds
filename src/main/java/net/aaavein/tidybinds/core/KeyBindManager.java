package net.aaavein.tidybinds.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.aaavein.tidybinds.TidyBinds;
import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.aaavein.tidybinds.util.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.*;

public final class KeyBindManager {

    public static final String HIDDEN_CATEGORY = "key.categories.tidybinds.hidden";

    private static final Map<KeyMapping, String> originalCategories = new HashMap<>();
    private static boolean hasSnapshotted = false;

    private KeyBindManager() {}

    public static KeyMapping getKeyByName(String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return null;

        for (KeyMapping key : mc.options.keyMappings) {
            if (key.getName().equals(name)) {
                return key;
            }
        }
        return null;
    }

    public static Set<String> getHiddenKeyNames() {
        Set<String> hidden = new HashSet<>(TidyBindsConfig.HIDDEN_KEYS.get());

        // add linked keys from combinations
        for (String combo : TidyBindsConfig.KEY_COMBINATIONS.get()) {
            String[] parts = combo.split(";", 2);
            if (parts.length == 2) {
                for (String link : parts[1].split(",")) {
                    hidden.add(link.trim());
                }
            }
        }

        return hidden;
    }

    public static void applyConfiguration() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        if (TidyBindsConfig.LOG_ACTIONS.get()) {
            TidyBinds.LOGGER.info("Applying TidyBinds configuration...");
        }

        snapshotOriginalCategories(mc);

        resetCategories();

        applyCategoryMoves();

        Set<String> hiddenKeys = getHiddenKeyNames();
        if (TidyBindsConfig.DISPLAY_HIDDEN_KEYS.get()) {
            moveToHiddenCategory(mc, hiddenKeys);
        }

        if (TidyBindsConfig.UNBIND_HIDDEN_KEYS.get()) {
            unbindHiddenKeys(mc, hiddenKeys);
        }

        applyCategoryOrdering();

        registerNewCategories(mc);

        if (TidyBindsConfig.PRINT_KEYS.get()) {
            printKeys(mc);
        }
        if (TidyBindsConfig.PRINT_CATEGORIES.get()) {
            printCategories();
        }
    }

    private static void snapshotOriginalCategories(Minecraft mc) {
        if (hasSnapshotted) return;

        for (KeyMapping key : mc.options.keyMappings) {
            originalCategories.put(key, key.getCategory());
        }
        hasSnapshotted = true;
    }

    private static void resetCategories() {
        for (Map.Entry<KeyMapping, String> entry : originalCategories.entrySet()) {
            KeyMappingHelper.setCategory(entry.getKey(), entry.getValue());
        }
    }

    private static void applyCategoryMoves() {
        for (String entry : TidyBindsConfig.KEY_CATEGORIES.get()) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) continue;

            String keyName = parts[0].trim();
            String category = parts[1].trim();

            KeyMapping key = getKeyByName(keyName);
            if (key != null) {
                KeyMappingHelper.setCategory(key, category);
                if (TidyBindsConfig.LOG_ACTIONS.get()) {
                    TidyBinds.LOGGER.info("Moved key '{}' to category '{}'", keyName, category);
                }
            }
        }
    }

    private static void moveToHiddenCategory(Minecraft mc, Set<String> hiddenKeys) {
        Set<String> categories = KeyMappingHelper.getCategoriesSet();
        if (categories != null) {
            categories.add(HIDDEN_CATEGORY);
        }

        for (KeyMapping key : mc.options.keyMappings) {
            if (hiddenKeys.contains(key.getName())) {
                KeyMappingHelper.setCategory(key, HIDDEN_CATEGORY);
                if (TidyBindsConfig.LOG_ACTIONS.get()) {
                    TidyBinds.LOGGER.info("Moved hidden key '{}' to hidden category", key.getName());
                }
            }
        }
    }

    private static void unbindHiddenKeys(Minecraft mc, Set<String> hiddenKeys) {
        for (KeyMapping key : mc.options.keyMappings) {
            if (hiddenKeys.contains(key.getName())) {
                key.setKey(InputConstants.UNKNOWN);
                if (TidyBindsConfig.LOG_ACTIONS.get()) {
                    TidyBinds.LOGGER.info("Unbound hidden key '{}'", key.getName());
                }
            }
        }
    }

    private static void applyCategoryOrdering() {
        Map<String, Integer> sortOrder = KeyMappingHelper.getSortOrderMap();
        Set<String> categories = KeyMappingHelper.getCategoriesSet();

        if (sortOrder == null || categories == null) return;

        for (String entry : TidyBindsConfig.CATEGORY_ORDER.get()) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) continue;

            try {
                String category = parts[0].trim();
                int order = Integer.parseInt(parts[1].trim());
                sortOrder.put(category, order);

                if (TidyBindsConfig.LOG_ACTIONS.get()) {
                    TidyBinds.LOGGER.info("Set category '{}' order to {}", category, order);
                }
            } catch (NumberFormatException e) {
                TidyBinds.LOGGER.warn("Invalid category order value: {}", entry);
            }
        }

        categories.clear();
        categories.addAll(sortOrder.keySet());
    }

    private static void registerNewCategories(Minecraft mc) {
        Set<String> categories = KeyMappingHelper.getCategoriesSet();
        if (categories == null) return;

        for (KeyMapping key : mc.options.keyMappings) {
            categories.add(key.getCategory());
        }
    }

    private static void printKeys(Minecraft mc) {
        TidyBinds.LOGGER.info("Available keys:");
        Arrays.stream(mc.options.keyMappings)
                .map(KeyMapping::getName)
                .sorted()
                .forEach(name -> TidyBinds.LOGGER.info("  {}", name));
    }

    private static void printCategories() {
        TidyBinds.LOGGER.info("Available categories:");
        Set<String> categories = KeyMappingHelper.getCategoriesSet();
        if (categories != null) {
            categories.stream()
                    .sorted()
                    .forEach(name -> TidyBinds.LOGGER.info("  {}", name));
        }
    }
}