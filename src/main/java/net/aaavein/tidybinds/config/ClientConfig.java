package net.aaavein.tidybinds.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> KEY_CATEGORIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CATEGORY_ORDER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HIDE_KEYS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> KEY_COMBINATIONS;

    public static final ModConfigSpec.BooleanValue PRINT_KEYS;
    public static final ModConfigSpec.BooleanValue PRINT_CATEGORIES;
    public static final ModConfigSpec.BooleanValue LOG_ACTIONS;
    public static final ModConfigSpec.BooleanValue SHOW_HIDDEN_KEYS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("General");
        KEY_CATEGORIES = builder
                .comment(" Move specific keys into a custom or existing categories.", " Format: \"key;category\"", " Example: \"key.jump;key.categories.inventory\"", " If you use a new category, you should translate it using a resource pack.", " To get the name of a category, use the \"Print Categories\" feature.")
                .defineListAllowEmpty("key_categories", new ArrayList<>(), () -> "", e -> true);

        CATEGORY_ORDER = builder
                .comment(" Define the order of categories in the \"Key Binds\" screen.", " Format: \"category;index\"", " Example: \"key.categories.inventory;1\"", " Lower numbers appear higher in the list.", " To get the name of a category, use the \"Print Categories\" feature.")
                .defineListAllowEmpty("category_order", new ArrayList<>(), () -> "", e -> true);

        HIDE_KEYS = builder
                .comment(" Define a list of keys to completely remove from the \"Key Binds\" screen.", " Format: \"key\"", " Example: \"key.jump\"", " To get the name of a key, use the \"Print Keys\" feature.")
                .defineListAllowEmpty("hide_keys", new ArrayList<>(), () -> "", e -> true);

        KEY_COMBINATIONS = builder
                .comment(" Trigger multiple keys via one button.", " Format: \"trigger_key;linked_key,another_linked_key\"", " Example: \"key.jump;key.sneak;key.right\"", " Linked keys become hidden.", " To get the name of a key, use the \"Print Keys\" feature.")
                .defineListAllowEmpty("key_combinations", new ArrayList<>(), () -> "", e -> true);
        builder.pop();

        builder.push("Debug");
        LOG_ACTIONS = builder.comment(" Logs Tidy Binds actions (e.g., \"Moving key X to category Y\") to the console.").define("log_actions", true);
        SHOW_HIDDEN_KEYS = builder.comment(" Shows hidden keys in a \"Hidden Keys\" category.").define("show_hidden_keys", false);
        PRINT_KEYS = builder.comment(" Prints a list of ALL available key names (e.g., \"key.attack\") to the log.").define("print_keys", false);
        PRINT_CATEGORIES = builder.comment(" Prints a list of ALL available category names (e.g., \"key.categories.inventory\") to the log.").define("print_categories", false);
        builder.pop();

        SPEC = builder.build();
    }
}