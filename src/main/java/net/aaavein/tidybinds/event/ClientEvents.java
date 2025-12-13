package net.aaavein.tidybinds.event;

import net.aaavein.tidybinds.TidyBinds;
import net.aaavein.tidybinds.core.DefaultKeyManager;
import net.aaavein.tidybinds.core.KeyBindManager;
import net.aaavein.tidybinds.core.KeyCombinationHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = TidyBinds.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ClientEvents {

    private static boolean initialized = false;

    private ClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (!initialized && mc.options != null) {
            initialize();
        }

        if (initialized && mc.player != null) {
            KeyCombinationHandler.tick();
        }
    }

    private static void initialize() {
        try {
            KeyBindManager.applyConfiguration();
            KeyCombinationHandler.reload();
            DefaultKeyManager.reload();
            initialized = true;
            TidyBinds.LOGGER.info("Tidy Binds initialized successfully");
        } catch (Exception e) {
            TidyBinds.LOGGER.error("Failed to initialize Tidy Binds", e);
        }
    }
}