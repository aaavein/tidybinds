package net.aaavein.tidybinds.util;

import net.aaavein.tidybinds.TidyBinds;
import net.minecraft.client.KeyMapping;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class KeyReflection {
    private static Field clickCountField;
    private static Field categorySortOrderField;
    private static Field knownCategoriesField;
    private static Field keyCategoryField;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        try {
            clickCountField = KeyMapping.class.getDeclaredField("clickCount");
            clickCountField.setAccessible(true);

            categorySortOrderField = KeyMapping.class.getDeclaredField("CATEGORY_SORT_ORDER");
            categorySortOrderField.setAccessible(true);

            knownCategoriesField = KeyMapping.class.getDeclaredField("CATEGORIES");
            knownCategoriesField.setAccessible(true);

            keyCategoryField = KeyMapping.class.getDeclaredField("category");
            keyCategoryField.setAccessible(true);

            initialized = true;
        } catch (Exception e) {
            TidyBinds.LOGGER.error("Failed to initialize KeyMapping reflection fields.", e);
        }
    }

    public static void setCategory(KeyMapping key, String newCategory) {
        if (!initialized) init();
        try {
            if (keyCategoryField != null) keyCategoryField.set(key, newCategory);
        } catch (IllegalAccessException e) {
            TidyBinds.LOGGER.error("Failed to set key category", e);
        }
    }

    public static int getClickCount(KeyMapping key) {
        if (!initialized) init();
        try {
            return clickCountField != null ? clickCountField.getInt(key) : 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    public static void setClickCount(KeyMapping key, int count) {
        if (!initialized) init();
        try {
            if (clickCountField != null) clickCountField.setInt(key, count);
        } catch (IllegalAccessException e) {
            // Suppress
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> getSortOrderMap() {
        if (!initialized) init();
        try {
            return (Map<String, Integer>) categorySortOrderField.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getCategoriesSet() {
        if (!initialized) init();
        try {
            return (Set<String>) knownCategoriesField.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}