package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
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
    public abstract int getMaxDamage();
    @Shadow
    public abstract int getDamage();
    
//    @Inject(method="getTooltip(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List",
    @Inject(method="getTooltip",            
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
    private void getTooltipdone(EntityPlayer playerIn, ITooltipFlag advanced, 
            CallbackInfoReturnable<List> ci,
            List<ITextComponent> list) {

        if (!advanced.isAdvanced() && !this.isEmpty()) {
            if (this.isDamaged()) {
                ITextComponent toolTip = new TextComponentTranslation("tooltip.durability",
                        (this.getMaxDamage() - this.getDamage())+
                        " / "+
                        this.getMaxDamage());
                toolTip=toolTip.applyTextStyle(ConfigurationHandler.getTooltipColor());
                if (!list.contains(toolTip)) {
                    list.add(toolTip);
                }
            }
        }
        ci.setReturnValue(list);
    }
}