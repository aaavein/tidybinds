package net.aaavein.tidybinds.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public final class TidyBindsConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> KEY_OVERRIDES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> KEY_COMBINATIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> KEY_CATEGORIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CATEGORY_ORDER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HIDDEN_KEYS;
    public static final ModConfigSpec.BooleanValue UNBIND_HIDDEN_KEYS;
    public static final ModConfigSpec.BooleanValue DISABLE_CONFLICTS;
    public static final ModConfigSpec.IntValue ENTRY_SPACING;

    public static final ModConfigSpec.BooleanValue LOG_ACTIONS;
    public static final ModConfigSpec.BooleanValue DISPLAY_HIDDEN_KEYS;
    public static final ModConfigSpec.BooleanValue PRINT_KEYS;
    public static final ModConfigSpec.BooleanValue PRINT_CATEGORIES;

    private TidyBindsConfig() {}

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("General");

        KEY_OVERRIDES = builder
                .comment(
                        " Override default keybinds. When Reset is pressed, keys reset to these values instead of vanilla defaults.",
                        " Format: key;input",
                        " Example: key.jump;key.keyboard.space",
                        " Use key.keyboard.unknown to set no default.",
                        " Use Print Keys to discover key names."
                )
                .defineListAllowEmpty("key_overrides", ArrayList::new, () -> "", TidyBindsConfig::isValidEntry);

        KEY_COMBINATIONS = builder
                .comment(
                        " Trigger multiple keys with a single button press.",
                        " Format: trigger_key;linked_key,another_linked_key",
                        " Example: key.jump;key.sneak,key.sprint",
                        " Linked keys are automatically hidden.",
                        " Use Print Keys to discover key names."
                )
                .defineListAllowEmpty("key_combinations", ArrayList::new, () -> "", TidyBindsConfig::isValidEntry);

        KEY_CATEGORIES = builder
                .comment(
                        " Move specific keys into custom or existing categories.",
                        " Format: key;category",
                        " Example: key.jump;key.categories.inventory",
                        " Custom categories should be translated via a resource pack.",
                        " Use Print Categories to discover category names."
                )
                .defineListAllowEmpty("key_categories", ArrayList::new, () -> "", TidyBindsConfig::isValidEntry);

        CATEGORY_ORDER = builder
                .comment(
                        " Define the display order of categories in the Key Binds screen.",
                        " Format: category;index",
                        " Example: key.categories.inventory;1",
                        " Lower indices appear higher in the list.",
                        " Use Print Categories to discover category names."
                )
                .defineListAllowEmpty("category_order", ArrayList::new, () -> "", TidyBindsConfig::isValidEntry);

        HIDDEN_KEYS = builder
                .comment(
                        " Keys to remove from the Key Binds screen.",
                        " Format: key",
                        " Example: key.jump",
                        " Use Print Keys to discover key names."
                )
                .defineListAllowEmpty("hidden_keys", ArrayList::new, () -> "", TidyBindsConfig::isValidString);

        UNBIND_HIDDEN_KEYS = builder
                .comment(
                        " Disable hidden keys by setting them to key.keyboard.unknown.",
                        " This prevents hidden keys from conflicting with visible bindings."
                )
                .define("unbind_hidden_keys", true);

        DISABLE_CONFLICTS = builder
                .comment(
                        " Disable the red highlighting on keybinds that share the same key.",
                        " Useful when you intentionally want multiple actions bound to one key."
                )
                .define("disable_conflicts", true);

        ENTRY_SPACING = builder
                .comment(
                        " Additional spacing (in pixels) between entries in the Key Binds screen."
                )
                .defineInRange("entry_spacing", 0, 0, 20);

        builder.pop();
        builder.push("Debug");

        LOG_ACTIONS = builder
                .comment(" Log Tidy Binds actions (e.g., Moving key X to category Y) to the console.")
                .define("log_actions", true);

        DISPLAY_HIDDEN_KEYS = builder
                .comment(" Display hidden keys in a special Hidden Keys category instead of removing them.")
                .define("display_hidden_keys", false);

        PRINT_KEYS = builder
                .comment(" Print all available key names (e.g., key.attack) to the console.")
                .define("print_keys", false);

        PRINT_CATEGORIES = builder
                .comment(" Print all available category names (e.g., key.categories.inventory) to the console.")
                .define("print_categories", false);

        builder.pop();

        SPEC = builder.build();
    }

    private static boolean isValidString(Object obj) {
        return obj instanceof String;
    }

    private static boolean isValidEntry(Object obj) {
        return obj instanceof String && ((String) obj).contains(";");
    }
}