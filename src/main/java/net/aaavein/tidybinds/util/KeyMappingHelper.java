package net.aaavein.tidybinds.util;

import net.aaavein.tidybinds.TidyBinds;
import net.minecraft.client.KeyMapping;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public final class KeyMappingHelper {

    private static Field clickCountField;
    private static Field categorySortOrderField;
    private static Field categoriesField;
    private static Field categoryField;

    private static boolean initialized = false;
    private static boolean initFailed = false;

    private KeyMappingHelper() {}

    private static void ensureInitialized() {
        if (initialized || initFailed) return;

        try {
            clickCountField = KeyMapping.class.getDeclaredField("clickCount");
            clickCountField.setAccessible(true);

            categorySortOrderField = KeyMapping.class.getDeclaredField("CATEGORY_SORT_ORDER");
            categorySortOrderField.setAccessible(true);

            categoriesField = KeyMapping.class.getDeclaredField("CATEGORIES");
            categoriesField.setAccessible(true);

            categoryField = KeyMapping.class.getDeclaredField("category");
            categoryField.setAccessible(true);

            initialized = true;
        } catch (NoSuchFieldException e) {
            TidyBinds.LOGGER.error("Failed to initialize KeyMapping reflection - field not found", e);
            initFailed = true;
        } catch (SecurityException e) {
            TidyBinds.LOGGER.error("Failed to initialize KeyMapping reflection - security exception", e);
            initFailed = true;
        }
    }

    public static void setCategory(KeyMapping key, String newCategory) {
        ensureInitialized();
        if (categoryField == null) return;

        try {
            categoryField.set(key, newCategory);
        } catch (IllegalAccessException e) {
            TidyBinds.LOGGER.error("Failed to set category for key '{}'", key.getName(), e);
        }
    }

    public static int getClickCount(KeyMapping key) {
        ensureInitialized();
        if (clickCountField == null) return 0;

        try {
            return clickCountField.getInt(key);
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    public static void setClickCount(KeyMapping key, int count) {
        ensureInitialized();
        if (clickCountField == null) return;

        try {
            clickCountField.setInt(key, count);
        } catch (IllegalAccessException e) {
            // silently ignore
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> getSortOrderMap() {
        ensureInitialized();
        if (categorySortOrderField == null) return null;

        try {
            return (Map<String, Integer>) categorySortOrderField.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getCategoriesSet() {
        ensureInitialized();
        if (categoriesField == null) return null;

        try {
            return (Set<String>) categoriesField.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}