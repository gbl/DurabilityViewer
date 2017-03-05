package de.guntram.mcmod.durabilityviewer.client.gui;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import static net.minecraftforge.common.config.Configuration.CATEGORY_CLIENT;
import net.minecraftforge.fml.client.config.GuiConfig;

public class DurabilityViewerGuiConfig extends GuiConfig {
    public DurabilityViewerGuiConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement(ConfigurationHandler.getConfig().getCategory(CATEGORY_CLIENT)).getChildElements(),
                DurabilityViewer.MODID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigurationHandler.getConfigFileName()));
    }
    
}
