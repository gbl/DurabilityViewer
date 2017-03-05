package de.guntram.mcmod.durabilityviewer.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.KeyHandler;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputEvent
{
    @SubscribeEvent
    public void keyPressed(final InputEvent.KeyInputEvent e) {
        if (KeyHandler.showHud.isPressed())
            GuiItemDurability.toggleVisibility();
    }
}
