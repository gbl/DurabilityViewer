package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class TooltipMixin {
    
    @Shadow
    public abstract boolean isEmpty();
    @Shadow
    public abstract boolean isDamaged();
    @Shadow
    public abstract int getDurability();
    @Shadow
    public abstract int getDamage();
    
//    @Inject(method="getTooltip(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List",
    @Inject(method="getTooltipText",            
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, 
            CallbackInfoReturnable<List> ci,
            List<TextComponent> list) {

        if (!advanced.isAdvanced() && !this.isEmpty()) {
            if (this.isDamaged()) {
                TextComponent toolTip = new TranslatableTextComponent("tooltip.durability",
                        (this.getDurability() - this.getDamage())+
                        " / "+
                        this.getDurability());
                toolTip=toolTip.applyFormat(ConfigurationHandler.getTooltipColor());
                if (!list.contains(toolTip)) {
                    list.add(toolTip);
                }
            }
        }
        ci.setReturnValue(list);
    }
}