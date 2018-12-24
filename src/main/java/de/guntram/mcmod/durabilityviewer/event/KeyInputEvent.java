package de.guntram.mcmod.durabilityviewer.event;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;

public class KeyInputEvent implements IHotkeyCallback
{
    @Override
    public boolean onKeyAction(KeyAction action, IKeybind ik) {
        GuiItemDurability.toggleVisibility();
        return true;
    }
}
