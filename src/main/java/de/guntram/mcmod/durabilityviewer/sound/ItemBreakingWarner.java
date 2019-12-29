/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;


/**
 *
 * @author gbl
 */
public class ItemBreakingWarner {
    private int lastDurability;
    private ItemStack lastStack;
    private static SoundEvent sound;
    
    public ItemBreakingWarner() {
        lastDurability=1000;
        lastStack=null;
        Identifier location;
        
        if (sound==null) {
            location=new Identifier(DurabilityViewer.MODID, "tool_breaking");
            sound=new SoundEvent(location);
        }
    }
    
    public boolean checkBreaks(ItemStack stack) {
        lastStack=stack;
        if (stack==null || !stack.isDamageable())
            return false;
        int newDurability=stack.getMaxDamage()-stack.getDamage();
        if (newDurability  < lastDurability
        && newDurability < ConfigurationHandler.getMinDurability()
        && newDurability * 100 / ConfigurationHandler.getMinPercent() < stack.getMaxDamage()) {
            lastDurability=newDurability;
            return true;
        }
        lastDurability=newDurability;
        return false;
    }
    
    public static void playWarningSound() {
        // System.out.append("playing warning sound");
        MinecraftClient.getInstance().player.playSound(sound, 100, 100);
    }
}
