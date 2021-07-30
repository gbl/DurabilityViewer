package de.guntram.mcmod.GBForgetools.GuiElements;


import com.mojang.blaze3d.vertex.PoseStack;
import de.guntram.mcmod.GBForgetools.GuiModOptions;
import de.guntram.mcmod.GBForgetools.Types.ConfigurationTrueColor;
import de.guntram.mcmod.GBForgetools.Types.SliderValueConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;


public class ColorPicker extends AbstractWidget implements SliderValueConsumer {

    private ColorDisplayAreaButton colorDisplay;
    private GuiSlider redSlider, greenSlider, blueSlider;
    private String option;
    private AbstractWidget element;
    private GuiModOptions optionScreen;
    private int currentColor;

    public ColorPicker(GuiModOptions optionScreen, int initialRGB, Component message) {
        super(0, 0, 250, 100, message);
        this.currentColor = initialRGB;
        this.optionScreen = optionScreen;
    }

    public void init() {
        TextComponent buttonITextComponent = new TextComponent("");
        this.x = (optionScreen.width - width) / 2;
        this.y = (optionScreen.height - height) / 2;
        colorDisplay = new ColorDisplayAreaButton(
            this, x, y, 20, 100, buttonITextComponent, currentColor
        );
        redSlider = new GuiSlider(this, x+50, y, 200, 20, (currentColor>>16)&0xff, 0, 255, "red");
        greenSlider = new GuiSlider(this, x+50, y+40, 200, 20, (currentColor>>16)&0xff, 0, 255, "green");
        blueSlider = new GuiSlider(this, x+50, y+80, 200, 20, (currentColor>>16)&0xff, 0, 255, "blue");
        visible = false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (visible) {
            if (redSlider.mouseClicked(mouseX, mouseY, button)
            ||  greenSlider.mouseClicked(mouseX, mouseY, button)
            ||  blueSlider.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            optionScreen.getFontRenderer().draw(stack, "R", x+30, y+10, 0xff0000);
            optionScreen.getFontRenderer().draw(stack, "G", x+30, y+50, 0x00ff00);
            optionScreen.getFontRenderer().draw(stack, "B", x+30, y+90, 0x0000ff);
            colorDisplay.render(stack, mouseX, mouseY, partialTicks);
            redSlider.render(stack, mouseX, mouseY, alpha);
            greenSlider.render(stack, mouseX, mouseY, alpha);
            blueSlider.render(stack, mouseX, mouseY, alpha);
        }
    }

    public void setLink(String option, AbstractWidget element) {
        this.option = option;
        this.element = element;
    }

    public void setCurrentColor(ConfigurationTrueColor color) {
        currentColor = color.getInt();
        colorDisplay.setColor(currentColor);
        redSlider.reinitialize(color.red);
        greenSlider.reinitialize(color.green);
        blueSlider.reinitialize(color.blue);
    }

    public ConfigurationTrueColor getCurrentColor() {
        return new ConfigurationTrueColor(currentColor);
    }

    public void onDoneButton() {
        optionScreen.onConfigChanging(option, new ConfigurationTrueColor(currentColor));
        element.setMessage(null);
        optionScreen.subscreenFinished();
    }

    @Override
    public void onConfigChanging(String color, Object value) {
        if (color.equals("red")) {
            currentColor = (currentColor & 0x00ffff) | ((int)(Integer)value) << 16;
        }
        else if (color.equals("green")) {
            currentColor = (currentColor & 0xff00ff) | ((int)(Integer)value) << 8;
        }
        else if (color.equals("blue")) {
            currentColor = (currentColor & 0xffff00) | ((int)(Integer)value);
        }
        colorDisplay.setColor(currentColor);
        optionScreen.onConfigChanging(option, new ConfigurationTrueColor(currentColor));
    }

    @Override
    public boolean wasMouseReleased() {
        return optionScreen.wasMouseReleased();
    }
    
    @Override
    public void setMouseReleased(boolean value) {
        optionScreen.setMouseReleased(value);
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
    }

    private class ColorDisplayAreaButton extends AbstractWidget {

        private final ColorPicker parent;
        private int rgb;

        public ColorDisplayAreaButton(ColorPicker parent, int x, int y, int width, int height, Component message, int rgb) {
            super(x, y, width, height, message);
            this.rgb = rgb;
            this.parent = parent;
        }
        
        public void setColor(int rgb) {
            this.rgb = rgb;
        }

        @Override
        protected void renderBg(PoseStack stack, Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                GuiComponent.fill(stack, x, y, x+width, y+height, rgb | 0xff000000);
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {
        }
    }
}
