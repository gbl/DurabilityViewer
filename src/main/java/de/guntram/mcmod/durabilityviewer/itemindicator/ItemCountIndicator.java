package de.guntram.mcmod.durabilityviewer.itemindicator;

import static de.guntram.mcmod.durabilityviewer.itemindicator.ItemIndicator.color_white;
import net.minecraft.item.ItemStack;

public class ItemCountIndicator implements ItemIndicator {
    final ItemStack stack;
    final int countOverride;
    
    public ItemCountIndicator(ItemStack stack) {
        this.stack=stack;
        countOverride=-1;
    }

    public ItemCountIndicator(ItemStack stack, int override) {
        this.stack=stack;
        countOverride=override;
    }

    @Override
    public String getDisplayValue() {
        return stack==null ? "0" : String.valueOf(countOverride==-1 ? stack.getAmount(): countOverride);
    }

    @Override
    public int getDisplayColor() {
        return color_white;
    }
    
    @Override
    public boolean isEmpty() {
        return stack==null || stack.isEmpty();
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
