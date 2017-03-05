/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

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
        if (sound==null) {
            sound=new SoundEvent(new ResourceLocation(DurabilityViewer.MODID, "tool_breaking"));
        }
    }
    
    public boolean checkBreaks(ItemStack stack) {
        lastStack=stack;
        if (stack==null || !stack.isItemStackDamageable())
            return false;
        int newDurability=stack.getMaxDamage()-stack.getItemDamage();
        if (newDurability < 100 && newDurability < lastDurability && newDurability*10 < stack.getMaxDamage()) {
            lastDurability=newDurability;
            return true;
        }
        lastDurability=newDurability;
        return false;
    }
    
    public static void playWarningSound() {
        System.out.append("playing warning sound");
        Minecraft.getMinecraft().player.playSound(sound, 100, 100);
    }
}
