package de.guntram.mcmod.fabrictools;

/*
 * This is to have class names that match Forge class names so people need
 * to change nothing but the imports.
 *
 * Whenever the user uses the GUI to change options, the 
 * ModConfigurationHandler's onConfigChanged() method is called with this.
 */

public class ConfigChangedEvent {

    public static class OnConfigChangedEvent extends ConfigChangedEvent {
        String mod;
        
        OnConfigChangedEvent(String mod) {
            this.mod=mod;
        }
        public String getModID() {
            return mod;
        };
    }
}
