package de.guntram.mcmod.durabilityviewer.client.gui;

import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.IModGuiFactory;

public class DurabilityViewerGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return DurabilityViewerGuiConfig.class;
    }
    
    @Override
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(final IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
}
