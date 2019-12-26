package de.guntram.mcmod.durabilityviewer.event;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.KeyHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputEvent
{
    @SubscribeEvent
    public void keyPressed(final InputEvent.KeyInputEvent e) {
        if (KeyHandler.showHud.isPressed())
            GuiItemDurability.toggleVisibility();
    }
}
