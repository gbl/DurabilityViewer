package de.guntram.mcmod.durabilityviewer.itemindicator;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventorySlotsIndicator implements ItemIndicator {

    final ItemStack stack;
    final int emptySlots;
    
    public InventorySlotsIndicator(PlayerInventory inventory) {
        stack=new ItemStack(Blocks.CHEST);
        int slots = 0;
        for (final ItemStack invitems : inventory.main) {
            if (invitems.isEmpty()) {
                slots++;
            }
        }
        emptySlots=slots;
    }
    

    @Override
    public String getDisplayValue() {
        return String.valueOf(emptySlots);
    }

    @Override
    public int getDisplayColor() {
        return color_white;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isItemStackDamageable() {
        return true;
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
}
