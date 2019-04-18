package de.guntram.mcmod.durabilityviewer.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.durabilityviewer.itemindicator.InventorySlotsIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemCountIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemDamageIndicator;
import de.guntram.mcmod.durabilityviewer.sound.ItemBreakingWarner;
import java.util.Collection;
import net.minecraft.client.util.Window;
import net.minecraft.item.BowItem;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;


public class GuiItemDurability
{
    private final MinecraftClient minecraft;
    private static boolean visible;
    private final TextRenderer fontRenderer;
    private final ItemRenderer itemRenderer;
    
    private static final int iconWidth=16;
    private static final int iconHeight=16;
    private static final int spacing=2;
    
    private ItemBreakingWarner mainHandWarner, offHandWarner, helmetWarner, chestWarner, pantsWarner, bootsWarner;
    
    public static void toggleVisibility() {
        visible=!visible;
    }
    
    public GuiItemDurability() {
        minecraft = MinecraftClient.getInstance();
        fontRenderer = minecraft.textRenderer;
        itemRenderer = minecraft.getItemRenderer();
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
        for (final ItemStack stack : minecraft.player.inventory.main) {
            if (isArrow(stack)) {
                arrows += stack.getAmount();
            }
        }
        return arrows;
    }
    
    private ItemStack getFirstArrowStack() {
        if (isArrow(minecraft.player.getOffHandStack())) {
            return minecraft.player.getOffHandStack();
        }
        if (isArrow(minecraft.player.getMainHandStack())) {
            return minecraft.player.getMainHandStack();
        }
        int size=minecraft.player.inventory.getInvSize();
        for (int i = 0; i < size; ++i) {
            final ItemStack itemstack = minecraft.player.inventory.getInvStack(i);
            if (this.isArrow(itemstack)) {
                return itemstack;
            }
        }
        return null;
    }
    
    private boolean isArrow(final ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }

    private class RenderSize {
        int width;
        int height;
        
        RenderSize(int w, int h) {
            width=w; height=h;
        }
    }

    public void onRenderGameOverlayPost(float partialTicks) {

        // This needs to be done before everything else to make sure
        // the title change that occurs when logging off gets through.
        String newTitle=DurabilityViewer.getAndResetChangedWindowTitle();
        if (newTitle!=null) {
            glfwSetWindowTitle(MinecraftClient.getInstance().window.getHandle(), newTitle);
        }
        
        if (!visible
        ||  minecraft.player.abilities.creativeMode)
            return;

        PlayerEntity player = (PlayerEntity) minecraft.player;
        boolean needToWarn=false;

        // @TODO: remove duplicate code
        ItemIndicator mainHand = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.HAND_MAIN));
        ItemIndicator offHand = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.HAND_OFF));
        ItemIndicator boots = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.FEET));
        ItemIndicator leggings = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.LEGS));
        ItemIndicator chestplate = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.CHEST));
        ItemIndicator helmet = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.HEAD));
        ItemIndicator arrows = null;
        ItemIndicator invSlots = new InventorySlotsIndicator(minecraft.player.inventory);
        
        needToWarn|=mainHandWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.HAND_MAIN));
        needToWarn|=offHandWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.HAND_OFF));
        needToWarn|=bootsWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.FEET));
        needToWarn|=pantsWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.LEGS));
        needToWarn|=chestWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.CHEST));
        needToWarn|=helmetWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.HEAD));
        if (needToWarn)
            ItemBreakingWarner.playWarningSound();
        
        if (mainHand.getItemStack().getItem() instanceof BowItem || offHand.getItemStack().getItem() instanceof BowItem) {
            arrows=new ItemCountIndicator(getFirstArrowStack(), getInventoryArrowCount());
        }
        // TODO Crossbows

        Window mainWindow = MinecraftClient.getInstance().window;
        
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
                xposArmor=mainWindow.getScaledWidth()-5-armorSize.width;
                xposTools=mainWindow.getScaledWidth()-5-armorSize.width-toolsSize.width;
                ypos=60;   // below buff/debuff effects
                break;
            case BOTTOM_LEFT:
                xposArmor=5;
                xposTools=5+armorSize.width;
                ypos=mainWindow.getScaledHeight()-5-totalHeight;
                break;
            case BOTTOM_RIGHT:
                xposArmor=mainWindow.getScaledWidth()-5-armorSize.width;
                xposTools=mainWindow.getScaledWidth()-5-armorSize.width-toolsSize.width;
                ypos=mainWindow.getScaledHeight()-5-totalHeight;
                break;
            default:
                return;
        }

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiLighting.enable();
        GuiLighting.enableForItems();

        this.renderItems(xposArmor, ypos, true, ConfigurationHandler.getCorner().isLeft(), armorSize.width, helmet, chestplate, leggings, boots);
        this.renderItems(xposTools, ypos, true, ConfigurationHandler.getCorner().isRight(), toolsSize.width, invSlots, mainHand, offHand, arrows);

        GuiLighting.disable();
        
        if (ConfigurationHandler.showEffectDuration()) {
            // a lot of this is copied from net/minecraft/client/gui/GuiIngame.java
            Collection<StatusEffectInstance> collection = minecraft.player.getStatusEffects();
            int posGood=0, posBad=0;
            for (StatusEffectInstance potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
                if (potioneffect.shouldShowIcon()) {
                    StatusEffect potion = potioneffect.getEffectType();
                    xpos=mainWindow.getScaledWidth();
                    if (potion.method_5573()) {     // isBeneficial
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
                    fontRenderer.draw(show, xpos+2, ypos, ItemIndicator.color_yellow);
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
                    itemRenderer.renderGuiItemIcon(item.getItemStack(), numbersLeft ? xpos+maxWidth-iconWidth-spacing : xpos, ypos+result.height);
                    fontRenderer.draw(displayString, numbersLeft? xpos : xpos+iconWidth+spacing, ypos+result.height+fontRenderer.fontHeight/2, color);
                }
                result.height+=16;
            }
        }
        if (result.width!=0)
            result.width+=iconWidth+spacing*2;
        return result;
    }
}