package net.aaavein.tidybinds.core;

import com.mojang.blaze3d.platform.InputConstants;
import net.aaavein.tidybinds.TidyBinds;
import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.aaavein.tidybinds.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public final class DefaultKeyManager {

    private static final Map<String, InputConstants.Key> originalDefaults = new HashMap<>();
    private static boolean hasSnapshotted = false;

    private DefaultKeyManager() {}

    public static void reload() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        snapshotOriginalDefaults(mc);

        resetToOriginalDefaults(mc);

        applyCustomDefaults(mc);
    }

    private static void snapshotOriginalDefaults(Minecraft mc) {
        if (hasSnapshotted) return;

        for (KeyMapping key : mc.options.keyMappings) {
            InputConstants.Key defaultKey = ((KeyMappingAccessor) key).tidybinds$getDefaultKey();
            originalDefaults.put(key.getName(), defaultKey);
        }
        hasSnapshotted = true;
    }

    private static void resetToOriginalDefaults(Minecraft mc) {
        for (KeyMapping key : mc.options.keyMappings) {
            InputConstants.Key originalDefault = originalDefaults.get(key.getName());
            if (originalDefault != null) {
                ((KeyMappingAccessor) key).tidybinds$setDefaultKey(originalDefault);
            }
        }
    }

    private static void applyCustomDefaults(Minecraft mc) {
        Map<String, InputConstants.Key> customDefaults = parseCustomDefaults();

        for (KeyMapping key : mc.options.keyMappings) {
            InputConstants.Key customDefault = customDefaults.get(key.getName());
            if (customDefault != null) {
                ((KeyMappingAccessor) key).tidybinds$setDefaultKey(customDefault);

                if (TidyBindsConfig.LOG_ACTIONS.get()) {
                    TidyBinds.LOGGER.info("Set custom default for '{}' to '{}'",
                            key.getName(), customDefault.getName());
                }
            }
        }
    }

    private static Map<String, InputConstants.Key> parseCustomDefaults() {
        Map<String, InputConstants.Key> result = new HashMap<>();

        for (String entry : TidyBindsConfig.KEY_OVERRIDES.get()) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) {
                TidyBinds.LOGGER.warn("Invalid default override format: {}", entry);
                continue;
            }

            String keyName = parts[0].trim();
            String inputName = parts[1].trim();

            try {
                InputConstants.Key input = parseInput(inputName);
                if (input != null) {
                    result.put(keyName, input);
                }
            } catch (Exception e) {
                TidyBinds.LOGGER.warn("Failed to parse input '{}' for key '{}': {}",
                        inputName, keyName, e.getMessage());
            }
        }

        return result;
    }

    private static InputConstants.Key parseInput(String inputName) {
        if (inputName == null || inputName.isEmpty()) {
            return InputConstants.UNKNOWN;
        }

        String normalizedInput = inputName.toLowerCase().trim();

        if (normalizedInput.equals("key.keyboard.unknown") ||
                normalizedInput.equals("unknown") ||
                normalizedInput.equals("none")) {
            return InputConstants.UNKNOWN;
        }

        if (normalizedInput.startsWith("key.keyboard.")) {
            String keyPart = inputName.substring("key.keyboard.".length());
            return parseKeyboardKey(keyPart);
        }

        if (normalizedInput.startsWith("key.mouse.")) {
            String buttonPart = inputName.substring("key.mouse.".length());
            return parseMouseButton(buttonPart);
        }

        try {
            return InputConstants.getKey(inputName);
        } catch (Exception ignored) {}

        TidyBinds.LOGGER.warn("Unknown input format: {}", inputName);
        return null;
    }

    private static InputConstants.Key parseKeyboardKey(String keyPart) {
        String upperKey = keyPart.toUpperCase();
        int keyCode = getGLFWKeyCode(upperKey);

        if (keyCode != -1) {
            return InputConstants.Type.KEYSYM.getOrCreate(keyCode);
        }

        try {
            return InputConstants.getKey("key.keyboard." + keyPart);
        } catch (Exception ignored) {}

        return null;
    }

    private static InputConstants.Key parseMouseButton(String buttonPart) {
        int button = switch (buttonPart.toLowerCase()) {
            case "left" -> 0;
            case "right" -> 1;
            case "middle" -> 2;
            case "4", "button4" -> 3;
            case "5", "button5" -> 4;
            default -> {
                try {
                    yield Integer.parseInt(buttonPart);
                } catch (NumberFormatException e) {
                    yield -1;
                }
            }
        };

        if (button >= 0) {
            return InputConstants.Type.MOUSE.getOrCreate(button);
        }
        return null;
    }

    private static int getGLFWKeyCode(String keyName) {
        return switch (keyName) {
            case "A" -> 65;
            case "B" -> 66;
            case "C" -> 67;
            case "D" -> 68;
            case "E" -> 69;
            case "F" -> 70;
            case "G" -> 71;
            case "H" -> 72;
            case "I" -> 73;
            case "J" -> 74;
            case "K" -> 75;
            case "L" -> 76;
            case "M" -> 77;
            case "N" -> 78;
            case "O" -> 79;
            case "P" -> 80;
            case "Q" -> 81;
            case "R" -> 82;
            case "S" -> 83;
            case "T" -> 84;
            case "U" -> 85;
            case "V" -> 86;
            case "W" -> 87;
            case "X" -> 88;
            case "Y" -> 89;
            case "Z" -> 90;

            case "0" -> 48;
            case "1" -> 49;
            case "2" -> 50;
            case "3" -> 51;
            case "4" -> 52;
            case "5" -> 53;
            case "6" -> 54;
            case "7" -> 55;
            case "8" -> 56;
            case "9" -> 57;

            case "F1" -> 290;
            case "F2" -> 291;
            case "F3" -> 292;
            case "F4" -> 293;
            case "F5" -> 294;
            case "F6" -> 295;
            case "F7" -> 296;
            case "F8" -> 297;
            case "F9" -> 298;
            case "F10" -> 299;
            case "F11" -> 300;
            case "F12" -> 301;
            case "F13" -> 302;
            case "F14" -> 303;
            case "F15" -> 304;
            case "F16" -> 305;
            case "F17" -> 306;
            case "F18" -> 307;
            case "F19" -> 308;
            case "F20" -> 309;
            case "F21" -> 310;
            case "F22" -> 311;
            case "F23" -> 312;
            case "F24" -> 313;
            case "F25" -> 314;

            case "SPACE" -> 32;
            case "APOSTROPHE", "'" -> 39;
            case "COMMA", "," -> 44;
            case "MINUS", "-" -> 45;
            case "PERIOD", "." -> 46;
            case "SLASH", "/" -> 47;
            case "SEMICOLON", ";" -> 59;
            case "EQUAL", "=" -> 61;
            case "LEFT_BRACKET", "[" -> 91;
            case "BACKSLASH", "\\" -> 92;
            case "RIGHT_BRACKET", "]" -> 93;
            case "GRAVE_ACCENT", "GRAVE", "`" -> 96;

            case "ESCAPE", "ESC" -> 256;
            case "ENTER", "RETURN" -> 257;
            case "TAB" -> 258;
            case "BACKSPACE" -> 259;
            case "INSERT" -> 260;
            case "DELETE" -> 261;
            case "RIGHT" -> 262;
            case "LEFT" -> 263;
            case "DOWN" -> 264;
            case "UP" -> 265;
            case "PAGE_UP", "PAGEUP" -> 266;
            case "PAGE_DOWN", "PAGEDOWN" -> 267;
            case "HOME" -> 268;
            case "END" -> 269;
            case "CAPS_LOCK", "CAPSLOCK" -> 280;
            case "SCROLL_LOCK", "SCROLLLOCK" -> 281;
            case "NUM_LOCK", "NUMLOCK" -> 282;
            case "PRINT_SCREEN", "PRINTSCREEN" -> 283;
            case "PAUSE" -> 284;

            case "KP_0", "KEYPAD_0", "NUMPAD_0" -> 320;
            case "KP_1", "KEYPAD_1", "NUMPAD_1" -> 321;
            case "KP_2", "KEYPAD_2", "NUMPAD_2" -> 322;
            case "KP_3", "KEYPAD_3", "NUMPAD_3" -> 323;
            case "KP_4", "KEYPAD_4", "NUMPAD_4" -> 324;
            case "KP_5", "KEYPAD_5", "NUMPAD_5" -> 325;
            case "KP_6", "KEYPAD_6", "NUMPAD_6" -> 326;
            case "KP_7", "KEYPAD_7", "NUMPAD_7" -> 327;
            case "KP_8", "KEYPAD_8", "NUMPAD_8" -> 328;
            case "KP_9", "KEYPAD_9", "NUMPAD_9" -> 329;
            case "KP_DECIMAL", "KEYPAD_DECIMAL" -> 330;
            case "KP_DIVIDE", "KEYPAD_DIVIDE" -> 331;
            case "KP_MULTIPLY", "KEYPAD_MULTIPLY" -> 332;
            case "KP_SUBTRACT", "KEYPAD_SUBTRACT" -> 333;
            case "KP_ADD", "KEYPAD_ADD" -> 334;
            case "KP_ENTER", "KEYPAD_ENTER" -> 335;
            case "KP_EQUAL", "KEYPAD_EQUAL" -> 336;

            case "LEFT_SHIFT", "LSHIFT" -> 340;
            case "LEFT_CONTROL", "LCONTROL", "LCTRL", "LEFT_CTRL" -> 341;
            case "LEFT_ALT", "LALT" -> 342;
            case "LEFT_SUPER", "LSUPER", "LEFT_WIN" -> 343;
            case "RIGHT_SHIFT", "RSHIFT" -> 344;
            case "RIGHT_CONTROL", "RCONTROL", "RCTRL", "RIGHT_CTRL" -> 345;
            case "RIGHT_ALT", "RALT" -> 346;
            case "RIGHT_SUPER", "RSUPER", "RIGHT_WIN" -> 347;
            case "MENU" -> 348;

            default -> -1;
        };
    }
}