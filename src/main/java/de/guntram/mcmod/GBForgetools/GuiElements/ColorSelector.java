package de.guntram.mcmod.GBForgetools.GuiElements;

import com.mojang.blaze3d.vertex.PoseStack;
import de.guntram.mcmod.GBForgetools.GuiModOptions;
import de.guntram.mcmod.GBForgetools.Types.ConfigurationMinecraftColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ColorSelector extends AbstractWidget {

    private ColorButton buttons[];
    private ConfigurationMinecraftColor currentColor;
    private String option;
    private AbstractWidget element;
    private GuiModOptions optionScreen;

    private int standardColors[] = { 
        0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
        0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA, 
        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
        0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    public ColorSelector(GuiModOptions optionScreen, MutableComponent message) {
        super(0, 0, 120, 120, message);
        buttons = new ColorButton[16];
        this.optionScreen = optionScreen;
    }

    public void init() {
        MutableComponent buttonText = Component.literal("");
        this.x = (optionScreen.width - width) / 2;
        this.y = (optionScreen.height - height) / 2;
        for (int i=0; i<16; i++) {
            buttons[i]=new ColorButton(
                this, x + (i/4) * 25, y + (i%4)*25, 20, 20, buttonText, i, standardColors[i]
            );
        }
        visible = false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (visible) {
            for (int i=0; i<buttons.length; i++) {
                if (buttons[i].mouseClicked(mouseX, mouseY, button))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            // renderButton(stack, mouseX, mouseY, partialTicks);
            for (int i=0; i<16; i++) {
                buttons[i].render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public void setLink(String option, AbstractWidget element) {
        this.option = option;
        this.element = element;
    }

    public void setCurrentColor(ConfigurationMinecraftColor color) {
        currentColor = color;
    }

    public ConfigurationMinecraftColor getCurrentColor() {
        return currentColor;
    }

    public void onColorSelected(int color) {
        currentColor.colorIndex = color;
        optionScreen.onConfigChanging(option, currentColor);
        element.setMessage(null);
        optionScreen.subscreenFinished();
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
    }

    private class ColorButton extends AbstractWidget {

        private final ColorSelector parent;
        private final int index;
        private final int color;

        public ColorButton(ColorSelector parent, int x, int y, int width, int height, MutableComponent message, int index, int color) {
            super(x, y, width, height, message);
            this.index = index;
            this.color = color;
            this.parent = parent;
        }

        @Override
        protected void renderBg(PoseStack stack, Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                super.renderBg(stack, mc, mouseX, mouseY);

                int x1=this.x+3;
                int x2=this.x+this.width-3;
                int y1=this.y+3;
                int y2=this.y+this.height-3;
                if (index == parent.getCurrentColor().colorIndex) {
                    
                    GuiComponent.fill(stack, x1, y1, x2, y2, 0xffffffff);
                    x1++; y1++; x2--; y2--;
                }
                GuiComponent.fill(stack, x1, y1, x2, y2, color | 0xff000000);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            // System.out.println("selected "+Integer.toHexString(color)+" from button "+this.index);
            parent.onColorSelected(this.index);
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {
        }
    }
}
