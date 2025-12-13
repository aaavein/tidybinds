package net.aaavein.tidybinds;

import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.aaavein.tidybinds.core.DefaultKeyManager;
import net.aaavein.tidybinds.core.KeyBindManager;
import net.aaavein.tidybinds.core.KeyCombinationHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = TidyBinds.MOD_ID, dist = Dist.CLIENT)
public class TidyBinds {

    public static final String MOD_ID = "tidybinds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TidyBinds(ModContainer container, IEventBus modBus) {
        container.registerConfig(ModConfig.Type.CLIENT, TidyBindsConfig.SPEC, "tidybinds.toml");
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            LOGGER.debug("TidyBinds config loaded");
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            LOGGER.info("TidyBinds config reloaded, refreshing configuration...");
            refreshConfiguration();
        }
    }

    public static void refreshConfiguration() {
        KeyBindManager.applyConfiguration();
        KeyCombinationHandler.reload();
        DefaultKeyManager.reload();
    }
}