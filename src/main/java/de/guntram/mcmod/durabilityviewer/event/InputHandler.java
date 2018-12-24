package de.guntram.mcmod.durabilityviewer.event;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

/**
 *
 * @author gbl
 */
public class InputHandler implements IKeybindProvider {

    public InputHandler() {
        super();
    }

    @Override
    public void addKeysToMap(IKeybindManager ikm) {
        ikm.addKeybindToMap(ConfigurationHandler.getHotkey());
    }

    @Override
    public void addHotkeys(IKeybindManager ikm) {
        ikm.addHotkeysForCategory("DurabilityViewer", "key.duraview", ConfigurationHandler.Hotkeys.HOTKEY_LIST);
    }
}
