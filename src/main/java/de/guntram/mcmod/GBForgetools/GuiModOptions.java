package de.guntram.mcmod.GBForgetools;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.GBForgetools.ConfigChangedEvent.OnConfigChangingEvent;
import de.guntram.mcmod.GBForgetools.GuiElements.ColorPicker;
import de.guntram.mcmod.GBForgetools.GuiElements.ColorSelector;
import de.guntram.mcmod.GBForgetools.GuiElements.GuiSlider;
import de.guntram.mcmod.GBForgetools.Types.ConfigurationMinecraftColor;
import de.guntram.mcmod.GBForgetools.Types.ConfigurationTrueColor;
import de.guntram.mcmod.GBForgetools.Types.SliderValueConsumer;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import static net.minecraft.client.gui.widget.Widget.WIDGETS_LOCATION;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiModOptions extends Screen implements Supplier<Screen>, SliderValueConsumer {
    
    private final Screen parent;
    private final String modName;
    private final ModConfigurationHandler handler;
    private final List<String> options;
    private final Logger LOGGER;
    
    private String screenTitle;
    
    private static final int LINEHEIGHT = 25;
    private static final int BUTTONHEIGHT = 20;
    private static final int TOP_BAR_SIZE = 40;
    private static final int BOTTOM_BAR_SIZE = 35;

    private boolean isDraggingScrollbar = false;
    private boolean mouseReleased = false;      // used to capture mouse release events when a child slider has the mouse dragging

    private int buttonWidth;
    private int scrollAmount;
    private int maxScroll;

    private static final ITextComponent trueText = new TranslationTextComponent("de.guntram.mcmod.fabrictools.true").mergeStyle(TextFormatting.GREEN);
    private static final ITextComponent falseText = new TranslationTextComponent("de.guntram.mcmod.fabrictools.false").mergeStyle(TextFormatting.RED);
    
    private ColorSelector colorSelector;
    private ColorPicker colorPicker;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GuiModOptions(Screen parent, String modName, ModConfigurationHandler confHandler) {
        super(new StringTextComponent(modName));
        this.parent=parent;
        this.modName=modName;
        this.handler=confHandler;
        this.screenTitle=modName+" Configuration";
        this.options=handler.getIConfig().getKeys();
        this.LOGGER=LogManager.getLogger();
        this.colorSelector = new ColorSelector(this, new StringTextComponent("Minecraft Color"));
        this.colorPicker = new ColorPicker(this, 0xffffff, new StringTextComponent("RGB Color"));
    }
    
    @Override
    protected void init() {
        buttonWidth = this.width / 2 -50;
        if (buttonWidth > 200) {
            buttonWidth = 200;
        }

        /* This button should always be in first position so it gets clicks
         * before any scrolled-out-of-visibility buttons do.
         */
        this.addButton(new Widget(this.width / 2 - 100, this.height - 27, 200, BUTTONHEIGHT, new TranslationTextComponent("gui.done")) {
            @Override
            public void onClick(double x, double y) {
                
                if (colorSelector.visible) {
                    subscreenFinished();
                    return;
                } else if (colorPicker.visible) {
                    colorPicker.onDoneButton();
                    return;
                }
                
                for (Widget button: buttons) {
                    if (button instanceof TextFieldWidget) {
                        if (button.isFocused()) {
                            button.changeFocus(false);
                        }
                    }
                }
                handler.onConfigChanged(new ConfigChangedEvent.OnConfigChangedEvent(modName));
                minecraft.displayGuiScreen(parent);
            }
        });

        int y=50-LINEHEIGHT;
        for (String option: options) {
            y+=LINEHEIGHT;
            Object value = handler.getIConfig().getValue(option);
            Widget element;
            if (value == null) {
                LogManager.getLogger().warn("value null, adding nothing");
                continue;
            } else if (handler.getIConfig().isSelectList(option)) {
                String[] options = handler.getIConfig().getListOptions(option);
                element = this.addButton(new Widget(this.width/2+10, y, buttonWidth, BUTTONHEIGHT, new TranslationTextComponent(options[(Integer)value])) {
                    @Override
                    public void onClick(double x, double y) {
                        int cur = (Integer) handler.getIConfig().getValue(option);
                        if (++cur == options.length) {
                            cur = 0;
                        }
                        onConfigChanging(option, cur);
                        this.changeFocus(true);
                    }
                    @Override
                    public void onFocusedChanged(boolean b) {
                        int cur = (Integer) handler.getIConfig().getValue(option);
                        this.setMessage(new TranslationTextComponent(options[cur]));
                        super.onFocusedChanged(b);
                    }
                });
            } else if (value instanceof Boolean) {
                element = this.addButton(new Widget(this.width/2+10, y, buttonWidth, BUTTONHEIGHT, (Boolean) value == true ? trueText : falseText) {
                    @Override
                    public void onClick(double x, double y) {
                        if ((Boolean)(handler.getIConfig().getValue(option))==true) {
                            onConfigChanging(option, false);
                        } else {
                            onConfigChanging(option, true);
                        }
                        this.changeFocus(true);
                    }
                    @Override
                    public void onFocusedChanged(boolean b) {
                        this.setMessage((Boolean) handler.getIConfig().getValue(option) == true ? trueText : falseText);
                        super.onFocusedChanged(b);
                    }
                });
            } else if (value instanceof String) {
                element=this.addButton(new TextFieldWidget(this.getFontRenderer(), this.width/2+10, y, buttonWidth, BUTTONHEIGHT, new StringTextComponent((String) value)) {
                    @Override
                    public void onFocusedChanged(boolean b) {
                        if (b) {
                            LOGGER.debug("value to textfield");
                            this.setText((String) handler.getIConfig().getValue(option));
                        } else {
                            LOGGER.debug("textfield to value");
                            handler.getIConfig().setValue(option, this.getText());
                        }
                        super.onFocusedChanged(b);
                    }
                    @Override
                    public boolean charTyped(char chr, int keyCode) {
                        boolean result = super.charTyped(chr, keyCode);
                        handler.onConfigChanging(new OnConfigChangingEvent(modName, option, this.getText()));
                        return result;
                    }

                    @Override
                    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
                        handler.onConfigChanging(new OnConfigChangingEvent(modName, option, this.getText()));
                        return result;
                    }
                    
                });
                ((TextFieldWidget) element).setMaxStringLength(120);
                element.changeFocus(false);
            } else if (value instanceof ConfigurationMinecraftColor
                   ||  value instanceof Integer && (Integer) handler.getIConfig().getMin(option) == 0 && (Integer) handler.getIConfig().getMax(option) == 15) {
                // upgrade int 0..15 from older mods to color
                if (value instanceof Integer) {
                    handler.getIConfig().setValue(option, new ConfigurationMinecraftColor((Integer)value));
                }
                element=this.addButton(new Widget(this.width/2+10, y, buttonWidth, BUTTONHEIGHT, StringTextComponent.EMPTY) {
                    @Override
                    public void onClick(double x, double y) {
                        enableColorSelector(option, this);
                    }
                    @Override
                    public void setMessage(ITextComponent ignored) {
                        Object o = handler.getIConfig().getValue(option);
                        int newIndex = ((ConfigurationMinecraftColor)o).colorIndex;
                        super.setMessage(new TranslationTextComponent("de.guntram.mcmod.fabrictools.color").mergeStyle(TextFormatting.fromColorIndex(newIndex)));
                    }
                    @Override
                    public boolean changeFocus(boolean ignored) { setMessage(null); return ignored; }
                });
                element.setMessage(StringTextComponent.EMPTY);
            } else if (value instanceof ConfigurationTrueColor
                    || value instanceof Integer && (Integer) handler.getIConfig().getMin(option) == 0 && (Integer) handler.getIConfig().getMax(option) == 0xffffff) {
                if (value instanceof Integer) {
                    handler.getIConfig().setValue(option, new ConfigurationTrueColor((Integer)value));
                }
                element=this.addButton(new Widget(this.width/2+10, y, buttonWidth, BUTTONHEIGHT, StringTextComponent.EMPTY) {
                    @Override
                    public void onClick(double x, double y) {
                        enableColorPicker(option, this);
                    }
                    @Override
                    public void setMessage(ITextComponent ignored) {
                        Object o = handler.getIConfig().getValue(option);
                        int rgb = ((ConfigurationTrueColor)o).getInt();
                        super.setMessage(new TranslationTextComponent("de.guntram.mcmod.fabrictools.color").setStyle(Style.EMPTY.setColor(Color.fromInt(rgb))));
                    }
                    @Override
                    public boolean changeFocus(boolean ignored) { setMessage(null); return ignored; }
                });
                element.setMessage(StringTextComponent.EMPTY);
            } else if (value instanceof Integer || value instanceof Float || value instanceof Double) {
                element=this.addButton(new GuiSlider(this, this.width/2+10, y, this.buttonWidth, BUTTONHEIGHT, handler.getIConfig(), option));
            } else {
                LogManager.getLogger().warn(modName +" has option "+option+" with data type "+value.getClass().getName());
                continue;
            }
            this.addButton(new Widget(this.width/2+10+buttonWidth+10, y, BUTTONHEIGHT, BUTTONHEIGHT, StringTextComponent.EMPTY) {
                @Override
                public void onClick(double x, double y) {
                    Object value = handler.getIConfig().getValue(option);
                    Object defValue = handler.getIConfig().getDefault(option);
                    if (value instanceof ConfigurationMinecraftColor) {
                        defValue = new ConfigurationMinecraftColor((int) defValue);
                    } else if (value instanceof ConfigurationTrueColor) {
                        defValue = new ConfigurationTrueColor((int) defValue);
                    }
                    onConfigChanging(option, defValue);
                    element.changeFocus(false);
                }
            });
        }
        maxScroll = (this.options.size())*LINEHEIGHT - (this.height - TOP_BAR_SIZE - BOTTOM_BAR_SIZE) + LINEHEIGHT;
        if (maxScroll < 0) {
            maxScroll = 0;
        }
        
        colorSelector.init();
        colorPicker.init();
        this.addButton(colorSelector);
        this.addButton(colorPicker);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.scrollAmount = (this.scrollAmount - (int)(amount * BUTTONHEIGHT / 2));
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        if (scrollAmount > maxScroll) {
            scrollAmount = maxScroll;
        }
        return true;
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);

        if (colorSelector.visible) {
            colorSelector.render(stack, mouseX, mouseY, partialTicks);
        } else if (colorPicker.visible) {
            colorPicker.render(stack, mouseX, mouseY, partialTicks);
        } else {
            int y = TOP_BAR_SIZE + LINEHEIGHT/2 - scrollAmount;
            for (int i=0; i<this.options.size(); i++) {
                if (y > TOP_BAR_SIZE - LINEHEIGHT/2 && y < height - BOTTOM_BAR_SIZE) {
                    font.drawString(stack, new TranslationTextComponent(options.get(i)).getString(), this.width / 2 -155, y+4, 0xffffff);
                    ((Widget)this.buttons.get(i*2+1)).y = y;                                          // config elem
                    ((Widget)this.buttons.get(i*2+1)).render(stack, mouseX, mouseY, partialTicks);
                    ((Widget)this.buttons.get(i*2+2)).y = y;                                        // reset button
                    ((Widget)this.buttons.get(i*2+2)).render(stack, mouseX, mouseY, partialTicks);
                }
                y += LINEHEIGHT;
            }

            y = TOP_BAR_SIZE + LINEHEIGHT/2 - scrollAmount;
            for (String text: options) {
                if (y > TOP_BAR_SIZE - LINEHEIGHT/2 && y < height - BOTTOM_BAR_SIZE) {
                    if (mouseX>this.width/2-155 && mouseX<this.width/2 && mouseY>y && mouseY<y+BUTTONHEIGHT) {
                        String ttText = handler.getIConfig().getTooltip(text);
                        if (ttText == null || ttText.isEmpty()) {
                            y += LINEHEIGHT;
                            continue;
                        }
                        TranslationTextComponent tooltip=new TranslationTextComponent(handler.getIConfig().getTooltip(text));
                        int width = font.getStringPropertyWidth(tooltip);
                        if (width == 0) {
                            // do nothing
                        } else if (width<=250) {
                            renderTooltip(stack, tooltip, 0, mouseY);
                        } else {
                            List<IReorderingProcessor> lines = font.trimStringToWidth(tooltip, 250);
                            renderTooltip(stack, lines, 0, mouseY);
                        }
                    }
                }
                y+=LINEHEIGHT;
            }

            if (maxScroll > 0) {
                // fill(stack, width-5, TOP_BAR_SIZE, width, height - BOTTOM_BAR_SIZE, 0xc0c0c0);
                int pos = (int)((height - TOP_BAR_SIZE - BOTTOM_BAR_SIZE - BUTTONHEIGHT) * ((float)scrollAmount / maxScroll));
                // fill(stack, width-5, pos, width, pos+BUTTONHEIGHT, 0x303030);
                this.minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
                blit(stack, width-5, pos+TOP_BAR_SIZE, 0, 66, 4, 20);
            }
        }
        this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
        RenderSystem.disableDepthTest();
        blit(stack, 0, 0, 0, 0, width, TOP_BAR_SIZE);
        blit(stack, 0, height-BOTTOM_BAR_SIZE, 0, 0, width, BOTTOM_BAR_SIZE);

        drawCenteredString(stack, font, screenTitle, this.width/2, (TOP_BAR_SIZE - font.FONT_HEIGHT)/2, 0xffffff);
        ((Widget)this.buttons.get(0)).render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingScrollbar = false;
        if (button == 0) {
            mouseReleased = true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && maxScroll > 0 && mouseX > width-5) {
            isDraggingScrollbar = true;
            scrollAmount = (int)((mouseY - TOP_BAR_SIZE)/(height - BOTTOM_BAR_SIZE - TOP_BAR_SIZE)*maxScroll);
            scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            scrollAmount = (int)((mouseY - TOP_BAR_SIZE)/(height - BOTTOM_BAR_SIZE - TOP_BAR_SIZE)*maxScroll);
            scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    private void enableColorSelector(String option, Widget element) {
        for (int i=1; i<buttons.size(); i++) {
            buttons.get(i).visible = false;
        }
        colorSelector.setCurrentColor((ConfigurationMinecraftColor) handler.getIConfig().getValue(option));
        colorSelector.visible = true;
        colorSelector.setLink(option, element);
    }

    private void enableColorPicker(String option, Widget element) {
        for (int i=1; i<buttons.size(); i++) {
            buttons.get(i).visible = false;
        }
        colorPicker.setCurrentColor((ConfigurationTrueColor) handler.getIConfig().getValue(option));
        colorPicker.visible = true;
        colorPicker.setLink(option, element);
    }
    
    public void subscreenFinished() {
        for (int i=1; i<buttons.size(); i++) {
            buttons.get(i).visible = true;
        }
        colorSelector.visible = false;
        colorPicker.visible = false;
    }
    
    @Override
    public boolean wasMouseReleased() {
        boolean result = mouseReleased;
        mouseReleased = false;
        return result;
    }
    
    @Override
    public void setMouseReleased(boolean value) {
        mouseReleased = value;
    }
    
    @Override
    public void onConfigChanging(String option, Object value) {
        handler.getIConfig().setValue(option, value);
        if (value instanceof ConfigurationMinecraftColor) {
            value = ((ConfigurationMinecraftColor)value).colorIndex;
        } else if (value instanceof ConfigurationTrueColor) {
            value = ((ConfigurationTrueColor)value).getInt();
        }
        handler.onConfigChanging(new ConfigChangedEvent.OnConfigChangingEvent(modName, option, value));
    }
    
    public FontRenderer getFontRenderer() {
        return font;
    }
}
