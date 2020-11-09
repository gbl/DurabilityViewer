package de.guntram.mcmod.GBForgetools;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.guntram.mcmod.GBForgetools.ConfigChangedEvent.OnConfigChangingEvent;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiModOptions extends Screen {
    
    private final Screen parent;
    private final String modName;
    private final ModConfigurationHandler handler;
    private final List<String> options;
    private final Logger LOGGER;
    
    private String screenTitle;
    
    private static final int LINEHEIGHT = 25;
    private static final int BUTTONHEIGHT = 20;
    
    private boolean mouseReleased = false;
    
    private static final IFormattableTextComponent trueText = new TranslationTextComponent("de.guntram.mcmod.fabrictools.true").mergeStyle(TextFormatting.GREEN);
    private static final IFormattableTextComponent falseText = new TranslationTextComponent("de.guntram.mcmod.fabrictools.false").mergeStyle(TextFormatting.RED);
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GuiModOptions(Screen parent, String modName, ModConfigurationHandler confHandler) {
        super(new StringTextComponent(modName));
        this.parent=parent;
        this.modName=modName;
        this.handler=confHandler;
        this.screenTitle=modName+" Configuration";
        this.options=handler.getConfig().getKeys();
        this.LOGGER=LogManager.getLogger();
    }
    
    @Override
    protected void init() {       // init
        this.addButton(new Widget(this.width / 2 - 100, this.height - 27, 200, 20, new TranslationTextComponent("gui.done")) {
            @Override
            public void onClick(double x, double y) {
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
        for (String text: options) {
            y+=LINEHEIGHT;
            Object value = handler.getConfig().getValue(text);
            Widget element;
            if (value == null) {
                LogManager.getLogger().warn("value null, adding nothing");
                continue;
            } else if (handler.getConfig().isSelectList(text)) {
                String[] options = handler.getConfig().getListOptions(text);
                element = this.addButton(new Widget(this.width/2+10, y, 200, BUTTONHEIGHT, new TranslationTextComponent(options[(Integer)value])) {
                    @Override
                    public void onClick(double x, double y) {
                        int cur = (Integer) handler.getConfig().getValue(text);
                        if (++cur == options.length) {
                            cur = 0;
                        }
                        handler.getConfig().setValue(text, (Integer)cur);
                        handler.onConfigChanging(new OnConfigChangingEvent(modName, text, cur));
                        this.changeFocus(true);
                    }
                    @Override
                    public void onFocusedChanged(boolean b) {
                        int cur = (Integer) handler.getConfig().getValue(text);
                        this.setMessage(new TranslationTextComponent(options[cur]));
                        super.onFocusedChanged(b);
                    }
                });
            } else if (value instanceof Boolean) {
                element = this.addButton(new Widget(this.width/2+10, y, 200, BUTTONHEIGHT, (Boolean) value == true ? trueText : falseText) {
                    @Override
                    public void onClick(double x, double y) {
                        if ((Boolean)(handler.getConfig().getValue(text))==true) {
                            handler.getConfig().setValue(text, false);
                            handler.onConfigChanging(new OnConfigChangingEvent(modName, text, false));
                        } else {
                            handler.getConfig().setValue(text, true);
                            handler.onConfigChanging(new OnConfigChangingEvent(modName, text, true));
                        }
                        this.changeFocus(true);
                    }
                    @Override
                    public void onFocusedChanged(boolean b) {
                        this.setMessage((Boolean) handler.getConfig().getValue(text) == true ? trueText : falseText);
                        super.onFocusedChanged(b);
                    }
                });
            } else if (value instanceof String) {
                element=this.addButton(new TextFieldWidget(this.font, this.width/2+10, y, 200, BUTTONHEIGHT, new StringTextComponent((String) value)) {
                    @Override
                    public void onFocusedChanged(boolean b) {
                        if (b) {
                            LOGGER.info("value to textfield");
                            this.setText((String) handler.getConfig().getValue(text));
                        } else {
                            LOGGER.info("textfield to value");
                            handler.getConfig().setValue(text, this.getText());
                        }
                        super.onFocusedChanged(b);
                    }
                    @Override
                    public boolean charTyped(char chr, int keyCode) {
                        boolean result = super.charTyped(chr, keyCode);
                        handler.onConfigChanging(new OnConfigChangingEvent(modName, text, this.getText()));
                        return result;
                    }

                    @Override
                    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
                        handler.onConfigChanging(new OnConfigChangingEvent(modName, text, this.getText()));
                        return result;
                    }
                    
                });
                element.changeFocus(false);
            } else if (value instanceof Integer || value instanceof Float || value instanceof Double) {
                element=this.addButton(new GuiSlider(this.width/2+10, y, handler.getConfig(), text));
            } else {
                LogManager.getLogger().warn(modName +" has option "+text+" with data type "+value.getClass().getName());
                continue;
            }
            this.addButton(new Widget(this.width/2+220, y, BUTTONHEIGHT, BUTTONHEIGHT, new StringTextComponent("")) {
                @Override
                public void onClick(double x, double y) {
                    handler.getConfig().setValue(text, handler.getConfig().getDefault(text));
                    handler.onConfigChanging(new OnConfigChangingEvent(modName, text, handler.getConfig().getDefault(text)));
                    element.changeFocus(false);
                }
            });
        }
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        drawCenteredString(stack, font, screenTitle, this.width/2, BUTTONHEIGHT, 0xffffff);
        super.render(stack, mouseX, mouseY, partialTicks);
        
        int y=50;
        for (String text: options) {
            font.func_238422_b_(stack, new TranslationTextComponent(text).func_241878_f(), this.width / 2 -155, y+2, 0xffffff);
            y+=LINEHEIGHT;
        }

        y=50;
        for (String text: options) {
            if (mouseX>this.width/2-155 && mouseX<this.width/2 && mouseY>y && mouseY<y+BUTTONHEIGHT) {
                TranslationTextComponent tooltip=new TranslationTextComponent(handler.getConfig().getTooltip(text));
                if (font.getStringPropertyWidth(tooltip)<=250) {
                    renderTooltip(stack, tooltip, mouseX, mouseY);
                } else {
                    List<IReorderingProcessor> lines = font.trimStringToWidth(tooltip, 250);
                    renderTooltip(stack, lines, mouseX, mouseY);
                }
            }
            y+=LINEHEIGHT;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            mouseReleased = true;
        }
        return super.mouseReleased(mouseX, mouseY, button); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    private enum Type {INT, FLOAT, DOUBLE;}
    
    private class GuiSlider extends Widget {
        Type type;
        boolean dragging;
        double sliderValue, min, max;
        Configuration config;
        String configOption;
        
        @SuppressWarnings("OverridableMethodCallInConstructor")
        GuiSlider(int x, int y, Configuration config, String option) {
            super(x, y, 200, BUTTONHEIGHT, new StringTextComponent("?"));
            Object value=config.getValue(option);
            if (value instanceof Double) {
                this.setMessage(new StringTextComponent(Double.toString((Double)value)));
                this.min=(Double)config.getMin(option);
                this.max=(Double)config.getMax(option);
                sliderValue=((Double)value-min)/(max-min);
                type=Type.DOUBLE;
            }
            else if (value instanceof Float) {
                this.setMessage(new StringTextComponent(Float.toString((Float)value)));
                this.min=(Float)config.getMin(option);
                this.max=(Float)config.getMax(option);
                sliderValue=((Float)value-min)/(max-min);
                type=Type.FLOAT;
            } else {
                this.setMessage(new StringTextComponent(Integer.toString((Integer)value)));
                this.min=(Integer)config.getMin(option);
                this.max=(Integer)config.getMax(option);
                sliderValue=((Integer)value-min)/(max-min);
                type=Type.INT;
            }

            this.config=config;
            this.configOption=option;
        }
        
        private void updateValue(double value) {
            switch (type) {
                case DOUBLE:
                    double doubleVal=value*(max-min)+min;
                    this.setMessage(new StringTextComponent(Double.toString(doubleVal)));
                    this.config.setValue(configOption, (Double) doubleVal);
                    handler.onConfigChanging(new OnConfigChangingEvent(modName, configOption, doubleVal));
                    break;
                case FLOAT:
                    float floatVal=(float) (value*(max-min)+min);
                    this.setMessage(new StringTextComponent(Float.toString(floatVal)));
                    this.config.setValue(configOption, (Float) floatVal);
                    handler.onConfigChanging(new OnConfigChangingEvent(modName, configOption, floatVal));
                    break;
                case INT:
                    int intVal=(int) (value*(max-min)+min);
                    this.setMessage(new StringTextComponent(Integer.toString(intVal)));
                    this.config.setValue(configOption, (Integer) intVal);
                    handler.onConfigChanging(new OnConfigChangingEvent(modName, configOption, intVal));
                    break;
            }
        }

        @Override
        protected void renderBg(MatrixStack stack, Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                if (this.dragging)
                {
                    this.sliderValue = (double)((float)(mouseX - (this.x + 4)) / (float)(this.width - 8));
                    this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
                    updateValue(this.sliderValue);
                    if (mouseReleased) {
                        this.dragging = false;
                        mouseReleased = false;
                    }
                            
                }
                mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.blit(stack, this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
                this.blit(stack, this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            }
        }

        /**
         * Called when the left mouse button is pressed over this button. This method is specific to Widget.
         */
        @Override
        public final void onClick(double mouseX, double mouseY)
        {
            this.sliderValue = (mouseX - (double)(this.x + 4)) / (double)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
            updateValue(sliderValue);
            this.dragging = true;
            mouseReleased = false;
        }

        /**
         * Called when the left mouse button is released. This method is specific to Widget.
         */
        @Override
        public void onRelease(double mouseX, double mouseY)
        {
            this.dragging = false;
        }
        
        @Override
        public void onFocusedChanged(boolean b) {
            Object value=config.getValue(configOption);
            if (value instanceof Double) {
                this.setMessage(new StringTextComponent(Double.toString((Double)value)));
                sliderValue=((Double)value-min)/(max-min);
            }
            else if (value instanceof Float) {
                this.setMessage(new StringTextComponent(Float.toString((Float)value)));
                sliderValue=((Float)value-min)/(max-min);
            } else {
                this.setMessage(new StringTextComponent(Integer.toString((Integer)value)));
                sliderValue=((Integer)value-min)/(max-min);
            }
            super.onFocusedChanged(b);
        }
    }
}
