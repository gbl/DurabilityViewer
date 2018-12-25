/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.client.gui;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import java.util.List;
import net.minecraft.client.resources.I18n;

/**
 *
 * @author gbl
 */
public class GuiConfig extends GuiConfigsBase {
    
    public GuiConfig() {
        super(10, 50, DurabilityViewer.MODID, null);
        title=I18n.format("key.categories.durabilityviewer");
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(ConfigurationHandler.Options.OPTIONS);
    }
}
