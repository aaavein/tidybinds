package net.aaavein.tidybinds.event;

import net.aaavein.tidybinds.TidyBinds;
import net.aaavein.tidybinds.core.InputHandler;
import net.aaavein.tidybinds.core.KeyBindsManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = TidyBinds.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientEventHandler {

    private static boolean initialized = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        // initialization logic (runs once as soon as options are loaded)
        // we do not check for mc.player here so this works in the main menu
        if (!initialized) {
            if (mc.options != null) {
                try {
                    KeyBindsManager.applyConfiguration();
                    InputHandler.reloadCombinations();
                    initialized = true;
                } catch (Exception e) {
                    TidyBinds.LOGGER.error("Failed to initialize TidyBinds", e);
                }
            }
        }

        // input handling (runs every tick, requires a player)
        if (initialized && mc.player != null) {
            InputHandler.handleTick();
        }
    }
}