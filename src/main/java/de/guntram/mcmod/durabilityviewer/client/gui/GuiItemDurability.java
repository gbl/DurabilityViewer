package de.guntram.mcmod.durabilityviewer.client.gui;

import com.google.common.collect.Ordering;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.durabilityviewer.itemindicator.InventorySlotsIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemCountIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemDamageIndicator;
import de.guntram.mcmod.durabilityviewer.sound.ItemBreakingWarner;
import java.util.Collection;
import net.minecraft.item.ItemBow;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.Display;

public class GuiItemDurability extends Gui
{
    private final Minecraft minecraft;
    private static boolean visible;
    private final FontRenderer fontRenderer;
    private final RenderItem itemRenderer;
    
    private static final int iconWidth=16;
    private static final int iconHeight=16;
    private static final int spacing=2;
    
    private ItemBreakingWarner mainHandWarner, offHandWarner, helmetWarner, chestWarner, pantsWarner, bootsWarner;
    
    public static void toggleVisibility() {
        visible=!visible;
    }
    
    public GuiItemDurability() {
        minecraft = Minecraft.getMinecraft();
        fontRenderer = minecraft.fontRenderer;
        itemRenderer = minecraft.getRenderItem();
        visible=true;
        
        mainHandWarner=new ItemBreakingWarner();
        offHandWarner=new ItemBreakingWarner();
        helmetWarner=new ItemBreakingWarner();
        chestWarner=new ItemBreakingWarner();
        pantsWarner=new ItemBreakingWarner();
        bootsWarner=new ItemBreakingWarner();
    }
    
    private int getInventoryArrowCount() {
        int arrows = 0;
        for (final ItemStack stack : minecraft.player.inventory.mainInventory) {
            if (isArrow(stack)) {
                arrows += stack.getCount();
            }
        }
        return arrows;
    }
    
    private ItemStack getFirstArrowStack() {
        if (isArrow(minecraft.player.getHeldItem(EnumHand.OFF_HAND))) {
            return minecraft.player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (isArrow(minecraft.player.getHeldItem(EnumHand.MAIN_HAND))) {
            return minecraft.player.getHeldItem(EnumHand.MAIN_HAND);
        }
        int size=minecraft.player.inventory.getSizeInventory();
        for (int i = 0; i < size; ++i) {
            final ItemStack itemstack = minecraft.player.inventory.getStackInSlot(i);
            if (this.isArrow(itemstack)) {
                return itemstack;
            }
        }
        return null;
    }
    
    private boolean isArrow(final ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemArrow;
    }
    
    private class RenderSize {
        int width;
        int height;
        
        RenderSize(int w, int h) {
            width=w; height=h;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRender(final RenderGameOverlayEvent.Post event) {

        // This needs to be done before everything else to make sure
        // the title change that occurs when logging off gets through.
        String newTitle=DurabilityViewer.getAndResetChangedWindowTitle();
        if (newTitle!=null)
            Display.setTitle(newTitle);

        
        if (!visible
        ||  event.isCanceled()
        ||  minecraft.player.capabilities.isCreativeMode
        ||  event.getType()!=RenderGameOverlayEvent.ElementType.POTION_ICONS)
            return;

        EntityPlayer effectivePlayer = (EntityPlayer) minecraft.player;
        boolean needToWarn=false;

        // @TODO: remove duplicate code
        ItemIndicator mainHand = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND));
        ItemIndicator offHand = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));
        ItemIndicator boots = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        ItemIndicator leggings = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        ItemIndicator chestplate = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        ItemIndicator helmet = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        ItemIndicator arrows = null;
        ItemIndicator invSlots = new InventorySlotsIndicator(minecraft.player.inventory);
        
        needToWarn|=mainHandWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND));
        needToWarn|=offHandWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));
        needToWarn|=bootsWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        needToWarn|=pantsWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        needToWarn|=chestWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        needToWarn|=helmetWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        if (needToWarn)
            ItemBreakingWarner.playWarningSound();
        
        if (mainHand.getItemStack().getItem() instanceof ItemBow || offHand.getItemStack().getItem() instanceof ItemBow) {
            arrows=new ItemCountIndicator(getFirstArrowStack(), getInventoryArrowCount());
        }
        ScaledResolution screensize=new ScaledResolution(minecraft);
        
        RenderSize armorSize=this.renderItems(0, 0, false, true, 0, boots, leggings, chestplate, helmet);
        RenderSize toolsSize=this.renderItems(0, 0, false, false, 0, invSlots, mainHand, offHand, arrows);
        
        int totalHeight=(toolsSize.height > armorSize.height ? toolsSize.height : armorSize.height);
        int totalWidth =(toolsSize.width  > armorSize.width  ? toolsSize.width  : armorSize.width);
        int xposArmor, xposTools, ypos, xpos;

        switch (ConfigurationHandler.getCorner()) {
            case TOP_LEFT:      
                xposArmor=5;
                xposTools=5+armorSize.width;
                ypos=5;
                break;
            case TOP_RIGHT:
                xposArmor=screensize.getScaledWidth()-5-armorSize.width;
                xposTools=screensize.getScaledWidth()-5-armorSize.width-toolsSize.width;
                ypos=60;   // below buff/debuff effects
                break;
            case BOTTOM_LEFT:
                xposArmor=5;
                xposTools=5+armorSize.width;
                ypos=screensize.getScaledHeight()-5-totalHeight;
                break;
            case BOTTOM_RIGHT:
                xposArmor=screensize.getScaledWidth()-5-armorSize.width;
                xposTools=screensize.getScaledWidth()-5-armorSize.width-toolsSize.width;
                ypos=screensize.getScaledHeight()-5-totalHeight;
                break;
            default:
                return;
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        this.renderItems(xposArmor, ypos, true, ConfigurationHandler.getCorner().isLeft(), armorSize.width, helmet, chestplate, leggings, boots);
        this.renderItems(xposTools, ypos, true, ConfigurationHandler.getCorner().isRight(), toolsSize.width, invSlots, mainHand, offHand, arrows);

        RenderHelper.disableStandardItemLighting();
        
        if (ConfigurationHandler.showEffectDuration()) {
            // a lot of this is copied from net/minecraft/client/gui/GuiIngame.java
            Collection<PotionEffect> collection = minecraft.player.getActivePotionEffects();
            int posGood=0, posBad=0;
            for (PotionEffect potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
                if (potioneffect.doesShowParticles()) {
                    Potion potion = potioneffect.getPotion();
                    xpos=screensize.getScaledWidth();
                    if (potion.isBeneficial()) {
                        posGood+=25; xpos-=posGood; ypos=15;
                    } else {
                        posBad+=25;  xpos-=posBad;  ypos=41;
                    }
                    int duration=potioneffect.getDuration();
                    String show;
                    if (duration > 1200)
                        show=(duration/1200)+"m";
                    else
                        show=(duration/20)+"s";
                    fontRenderer.drawString(show, xpos+2, ypos, ItemIndicator.color_yellow);
                }
            }
        }
    }
    
    private RenderSize renderItems(int xpos, int ypos, boolean reallyDraw, boolean numbersLeft, int maxWidth, ItemIndicator... items) {
        RenderSize result=new RenderSize(0, 0);
        
        for (ItemIndicator item: items) {
            if (item != null && !item.isEmpty() && item.isItemStackDamageable()) {
                String displayString=item.getDisplayValue();
                int width=fontRenderer.getStringWidth(displayString);
                if (width>result.width)
                    result.width=width;
                if (reallyDraw) {
                    int color=item.getDisplayColor();
                    itemRenderer.renderItemAndEffectIntoGUI(item.getItemStack(), numbersLeft ? xpos+maxWidth-iconWidth-spacing : xpos, ypos+result.height);
                    fontRenderer.drawString(displayString, numbersLeft? xpos : xpos+iconWidth+spacing, ypos+result.height+fontRenderer.FONT_HEIGHT/2, color);
                }
                result.height+=16;
            }
        }
        if (result.width!=0)
            result.width+=iconWidth+spacing*2;
        return result;
    }
}
