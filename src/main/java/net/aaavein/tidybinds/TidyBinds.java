package net.aaavein.tidybinds;

import net.aaavein.tidybinds.config.ClientConfig;
import net.aaavein.tidybinds.core.InputHandler;
import net.aaavein.tidybinds.core.KeyBindsManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent; // Import this
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = TidyBinds.MOD_ID, dist = Dist.CLIENT)
public class TidyBinds {
    public static final String MOD_ID = "tidybinds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TidyBinds(ModContainer container, IEventBus modBus) { // Inject ModBus
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // register the config reload listener
        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
    }

    // runs when config is loaded (startup)
    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            // we can't apply key logic here yet because minecraft options aren't loaded,
            // but we can prepare data if needed
            // actual application still happens in ClientEventHandler for the first run
        }
    }

    // runs when config is changed in game
    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            LOGGER.info("TidyBinds config reloaded, refreshing keys...");
            KeyBindsManager.applyConfiguration();
            InputHandler.reloadCombinations();
        }
    }
}