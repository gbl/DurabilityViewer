package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gbl
 */
public class ColytraBreakingWarner extends ItemBreakingWarner {

    private static int elytraMaxDamage;
    
    int lastDurability;
    
    @Override
    public boolean checkBreaks(ItemStack stack) {
        if (stack.getNbt() == null  || !stack.getNbt().contains("colytra:ElytraUpgrade")) {
            return false;
        }
        
        int damage;
        try {
            damage = stack.getNbt().getCompound("colytra:ElytraUpgrade").getCompound("tag").getInt("Damage");
        } catch (Exception ex) {
            return false;
        }
        
        if (elytraMaxDamage == 0) {
            elytraMaxDamage = new ItemStack(Items.ELYTRA).getMaxDamage();
        }

        int newDurability=elytraMaxDamage - damage;
        if (newDurability  < lastDurability
        && newDurability < ConfigurationHandler.getMinDurability()
        && newDurability * 100 / ConfigurationHandler.getMinPercent() < elytraMaxDamage) {
            lastDurability=newDurability;
            return true;
        }
        lastDurability=newDurability;
        return false;
    }
}
