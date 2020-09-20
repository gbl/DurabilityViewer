package de.guntram.mcmod.durabilityviewer.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.durabilityviewer.itemindicator.InventorySlotsIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemCountIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemDamageIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemIndicator;
import de.guntram.mcmod.durabilityviewer.sound.ItemBreakingWarner;
import java.util.Collection;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;

public class GuiItemDurability extends IngameGui
{
    private final Minecraft minecraft;
    private static boolean visible;
    private final FontRenderer fontRenderer;
    private final ItemRenderer itemRenderer;
    
    private static final int iconWidth=16;
    private static final int iconHeight=16;
    private static final int spacing=2;
    
    private ItemBreakingWarner mainHandWarner, offHandWarner, helmetWarner, chestWarner, pantsWarner, bootsWarner;
    
    public static void toggleVisibility() {
        visible=!visible;
    }
    
    public GuiItemDurability() {
        super(Minecraft.getInstance());
        minecraft = Minecraft.getInstance();
        fontRenderer = minecraft.fontRenderer;
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
        for (final ItemStack stack : minecraft.player.inventory.mainInventory) {
            if (isArrow(stack)) {
                arrows += stack.getCount();
            }
        }
        return arrows;
    }
    
    private ItemStack getFirstArrowStack() {
        if (isArrow(minecraft.player.getHeldItem(Hand.OFF_HAND))) {
            return minecraft.player.getHeldItem(Hand.OFF_HAND);
        }
        if (isArrow(minecraft.player.getHeldItem(Hand.MAIN_HAND))) {
            return minecraft.player.getHeldItem(Hand.MAIN_HAND);
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
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }
    
    private class RenderSize {
        int width;
        int height;
        
        RenderSize(int w, int h) {
            width=w; height=h;
        }
    }
    
    private enum RenderPos {
        left, over, right;
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRender(final RenderGameOverlayEvent.Post event) {

        // This needs to be done before everything else to make sure
        // the title change that occurs when logging off gets through.
        String newTitle=DurabilityViewer.getAndResetChangedWindowTitle();
        if (newTitle!=null) {
            glfwSetWindowTitle(minecraft.getMainWindow().getHandle(), newTitle);
        }

        
        if (!visible
        ||  event.isCanceled()
        // ||  minecraft.player.abilities.isCreativeMode
        ||  event.getType()!=RenderGameOverlayEvent.ElementType.POTION_ICONS)
            return;

        ClientPlayerEntity effectivePlayer = (ClientPlayerEntity) minecraft.player;
        boolean needToWarn=false;

        // @TODO: remove duplicate code
        ItemIndicator mainHand = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
        ItemIndicator offHand = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
        ItemIndicator boots = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.FEET));
        ItemIndicator leggings = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.LEGS));
        ItemIndicator chestplate = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.CHEST));
        ItemIndicator helmet = new ItemDamageIndicator(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.HEAD));
        ItemIndicator arrows = null;
        ItemIndicator invSlots = ConfigurationHandler.getShowChestIcon() ? new InventorySlotsIndicator(minecraft.player.inventory) : null;
        
        needToWarn|=mainHandWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
        needToWarn|=offHandWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
        needToWarn|=bootsWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.FEET));
        needToWarn|=pantsWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.LEGS));
        needToWarn|=chestWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.CHEST));
        needToWarn|=helmetWarner.checkBreaks(effectivePlayer.getItemStackFromSlot(EquipmentSlotType.HEAD));
        if (needToWarn)
            ItemBreakingWarner.playWarningSound();
        
        if (mainHand.getItemStack().getItem() instanceof BowItem || offHand.getItemStack().getItem() instanceof BowItem) {
            arrows=new ItemCountIndicator(getFirstArrowStack(), getInventoryArrowCount());
        }
        MainWindow mainWindow=Minecraft.getInstance().getMainWindow();
        MatrixStack stack = event.getMatrixStack();
        
        RenderSize armorSize, toolsSize;
        armorSize=this.renderItems(stack, 0, 0, false, RenderPos.left, 0, boots, leggings, chestplate, helmet);
        toolsSize=this.renderItems(stack, 0, 0, false, RenderPos.right, 0, invSlots, mainHand, offHand, arrows);
        
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

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (ConfigurationHandler.getArmorAroundHotbar()) {
            int leftOffset = -120;
            int rightOffset = 100;
            if (!effectivePlayer.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                if (minecraft.gameSettings.mainHand == HandSide.RIGHT) {
                    leftOffset -= 20;
                } else {
                    rightOffset += 20;
                }
            }
            int helmetTextWidth = fontRenderer.getStringWidth(helmet.getDisplayValue());
            int chestTextWidth = fontRenderer.getStringWidth(chestplate.getDisplayValue());
            this.renderItems(stack, mainWindow.getScaledWidth()/2+leftOffset - helmetTextWidth, mainWindow.getScaledHeight()-iconHeight*2-2, true, RenderPos.left, helmetTextWidth+iconWidth+spacing, helmet);
            this.renderItems(stack, mainWindow.getScaledWidth()/2+leftOffset - chestTextWidth, mainWindow.getScaledHeight()-iconHeight-2, true, RenderPos.left, chestTextWidth+iconWidth+spacing, chestplate);
            this.renderItems(stack, mainWindow.getScaledWidth()/2+rightOffset, mainWindow.getScaledHeight()-iconHeight*2-2, true, RenderPos.right, armorSize.width, leggings);
            this.renderItems(stack, mainWindow.getScaledWidth()/2+rightOffset, mainWindow.getScaledHeight()-iconHeight-2, true, RenderPos.right, armorSize.width, boots);
            if (ConfigurationHandler.getCorner().isRight()) {
                xposTools += armorSize.width;
            } else {
                xposTools -= armorSize.width;
            }
        } else {
            this.renderItems(stack, xposArmor, ypos, true, ConfigurationHandler.getCorner().isLeft() ? RenderPos.left : RenderPos.right, armorSize.width, helmet, chestplate, leggings, boots);
        }
        this.renderItems(stack, xposTools, ypos, true, ConfigurationHandler.getCorner().isRight() ? RenderPos.right : RenderPos.left, toolsSize.width, invSlots, mainHand, offHand, arrows);

        RenderHelper.disableStandardItemLighting();
        
        if (ConfigurationHandler.showEffectDuration()) {
            // a lot of this is copied from net/minecraft/client/gui/GuiIngame.java
            Collection<EffectInstance> collection = minecraft.player.getActivePotionEffects();
            int posGood=0, posBad=0;
            for (EffectInstance potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
                if (potioneffect.doesShowParticles()) {
                    Effect potion = potioneffect.getPotion();
                    xpos=mainWindow.getScaledWidth();
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
                    fontRenderer.func_238421_b_(event.getMatrixStack(), show, xpos+2, ypos, ItemIndicator.color_yellow);        // draw
                }
            }
        }
    }
    
    private RenderSize renderItems(MatrixStack stack, int xpos, int ypos, boolean reallyDraw, RenderPos numberPos, int maxWidth, ItemIndicator... items) {
        RenderSize result=new RenderSize(0, 0);
        
        for (ItemIndicator item: items) {
            if (item != null && !item.isEmpty() && item.isItemStackDamageable()) {
                String displayString=item.getDisplayValue();
                int width=fontRenderer.getStringWidth(displayString);
                if (width>result.width)
                    result.width=width;
                if (reallyDraw) {
                    int color=item.getDisplayColor();
                    itemRenderer.renderItemAndEffectIntoGUI(item.getItemStack(), numberPos == RenderPos.left ? xpos+maxWidth-iconWidth-spacing : xpos, ypos+result.height);
                    fontRenderer.func_238421_b_(stack, displayString, numberPos != RenderPos.right? xpos : xpos+iconWidth+spacing, ypos+result.height+fontRenderer.FONT_HEIGHT/2 + (numberPos==RenderPos.over ? 10  : 0), color);
                }
                result.height+=16;
            }
        }
        if (result.width!=0)
            result.width+=iconWidth+spacing*2;
        return result;
    }
}
