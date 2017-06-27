package de.guntram.mcmod.durabilityviewer.client.gui;

import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.IModGuiFactory;

public class DurabilityViewerGuiFactory implements IModGuiFactory
{
    @Override
    public boolean hasConfigGui() {
        return true;
    }
    
    @Override
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new DurabilityViewerGuiConfig(parentScreen);
    }
    
    @Override
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
