package de.guntram.mcmod.GBForgetools;

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
    
    public static class OnConfigChangingEvent extends ConfigChangedEvent {
        String mod, item;
        Object newValue;

        public OnConfigChangingEvent(String mod, String item, Object newValue) {
            this.mod = mod;
            this.item = item;
            this.newValue = newValue;
        }
        
        public String getModID() {
            return mod;
        }
        
        public String getItem() {
            return item;
        }
        
        public Object getNewValue() {
            return newValue;
        }
    }
}
