package de.guntram.mcmod.fabrictools.mixins;
/*
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import de.guntram.mcmod.fabrictools.GuiModOptions;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourcePackListEntryFound.class)
public abstract class MixinRenderResourcePackEntry extends GuiListExtended.IGuiListEntry<ResourcePackListEntryFound> {
    
    private ResourceLocation iconLoc;

    @Inject(method="drawEntry", at=@At(value="RETURN"))
    private void onDrawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks, CallbackInfo ci) {
        if (ConfigurationProvider.hasMod(this.getResourcePackName())) {
            if (iconLoc==null) {
                iconLoc=new ResourceLocation("textures/item/written_book.png");
            }
            int myX=this.getX();
            int myY=this.getY();
            int x = mouseX-myX;
            int y = mouseY-myY;
            if (x>0 && y>0 && x<32 && y<32) {
                this.client.getTextureManager().bindTexture(iconLoc);
                Gui.drawModalRectWithCustomSizedTexture(myX, myY, 0.0f, 0.0f, 16, 16, 16.0f, 16.0f);
                //System.out.println("Drawing "+this.getClass().getCanonicalName()+" with flag "+p_194999_5_);
            }
        }
    }
    
    @Inject(method="mouseClicked", at=@At(value="HEAD"), cancellable = true)
    private boolean onMouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable ci) {
        ModConfigurationHandler handler = ConfigurationProvider.getHandler(this.getResourcePackName());
        if (handler==null)
            return false;
        int x=(int)mouseX-this.getX();
        int y=(int)mouseY-this.getY();
        if (x>0 && x>0 && x<16 && y<16) {
            System.out.println("configuring "+this.getResourcePackName());
            Minecraft.getInstance().displayGuiScreen(new GuiModOptions(Minecraft.getInstance().currentScreen, this.getResourcePackName(), handler));
            ci.cancel();
            return true;
        }
        return false;
    }

    @Shadow private String getResourcePackName() { return ""; }
    @Shadow private Minecraft client;
}
*/