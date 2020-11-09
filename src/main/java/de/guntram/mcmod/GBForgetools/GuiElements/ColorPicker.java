package de.guntram.mcmod.GBForgetools.GuiElements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.guntram.mcmod.GBForgetools.GuiModOptions;
import de.guntram.mcmod.GBForgetools.Types.ConfigurationTrueColor;
import de.guntram.mcmod.GBForgetools.Types.SliderValueConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;


public class ColorPicker extends Widget implements SliderValueConsumer {

    private ColorDisplayAreaButton colorDisplay;
    private GuiSlider redSlider, greenSlider, blueSlider;
    private String option;
    private Widget element;
    private GuiModOptions optionScreen;
    private int currentColor;

    public ColorPicker(GuiModOptions optionScreen, int initialRGB, ITextComponent message) {
        super(0, 0, 250, 100, message);
        this.currentColor = initialRGB;
        this.optionScreen = optionScreen;
    }

    public void init() {
        ITextComponent buttonITextComponent = new StringTextComponent("");
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            optionScreen.getFontRenderer().drawString(stack, "R", x+30, y+10, 0xff0000);
            optionScreen.getFontRenderer().drawString(stack, "G", x+30, y+50, 0x00ff00);
            optionScreen.getFontRenderer().drawString(stack, "B", x+30, y+90, 0x0000ff);
            colorDisplay.render(stack, mouseX, mouseY, partialTicks);
            redSlider.render(stack, mouseX, mouseY, alpha);
            greenSlider.render(stack, mouseX, mouseY, alpha);
            blueSlider.render(stack, mouseX, mouseY, alpha);
        }
    }

    public void setLink(String option, Widget element) {
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

    private class ColorDisplayAreaButton extends Widget {

        private final ColorPicker parent;
        private int rgb;

        public ColorDisplayAreaButton(ColorPicker parent, int x, int y, int width, int height, ITextComponent message, int rgb) {
            super(x, y, width, height, message);
            this.rgb = rgb;
            this.parent = parent;
        }
        
        public void setColor(int rgb) {
            this.rgb = rgb;
        }

        @Override
        protected void renderBg(MatrixStack stack, Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                // super.renderBg(stack, mc, mouseX, mouseY); no this renders an unusable texture

                final Tessellator tessellator = Tessellator.getInstance();
                final BufferBuilder bufferBuilder = tessellator.getBuffer();
                float red = ((rgb >> 16)/255.0f);
                float green = (((rgb >> 8) & 0xff)/255.0f);
                float blue = (((rgb >> 0) & 0xff) /255.0f);

                GlStateManager.disableTexture();

                bufferBuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
                Matrix4f model = stack.getLast().getMatrix();
                int x1=this.x;
                int x2=this.x+this.width;
                int y1=this.y;
                int y2=this.y+this.height;
                bufferBuilder.pos(model, x1, y1, 0.0f).color(red, green, blue, 1.0f).endVertex();
                bufferBuilder.pos(model, x1, y2, 0.0f).color(red, green, blue, 1.0f).endVertex();
                bufferBuilder.pos(model, x2, y2, 0.0f).color(red, green, blue, 1.0f).endVertex();
                bufferBuilder.pos(model, x2, y1, 0.0f).color(red, green, blue, 1.0f).endVertex();
                tessellator.draw();

                GlStateManager.enableTexture();
            }
        }
    }
}
