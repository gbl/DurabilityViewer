package de.guntram.mcmod.durabilityviewer.handler;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import de.guntram.mcmod.fabrictools.GuiModOptions;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class MMConfigurationHandler implements ModMenuApi
{
    @Override
    public ConfigScreenFactory getModConfigScreenFactory() {
        return screen -> new GuiModOptions(screen, DurabilityViewer.MODNAME, ConfigurationProvider.getHandler(DurabilityViewer.MODNAME));
    }
}
