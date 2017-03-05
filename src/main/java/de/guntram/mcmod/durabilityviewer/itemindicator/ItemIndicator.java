package de.guntram.mcmod.durabilityviewer.itemindicator;

import java.awt.Color;
import net.minecraft.item.ItemStack;

public interface ItemIndicator {
    static int color_white=Color.WHITE.getRGB(),
            color_green=Color.GREEN.getRGB(),
            color_yellow=Color.YELLOW.getRGB(),
            color_red=Color.RED.getRGB();
    public String getDisplayValue();
    public int getDisplayColor();
    public boolean isEmpty();
    public boolean isItemStackDamageable();
    public ItemStack getItemStack();
}
