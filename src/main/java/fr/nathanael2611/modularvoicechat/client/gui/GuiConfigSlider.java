package fr.nathanael2611.modularvoicechat.client.gui;

import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.GlStateManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.MicroManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.SpeakerManager;
import fr.nathanael2611.modularvoicechat.config.ClientConfig;
import fr.nathanael2611.modularvoicechat.config.ConfigProperty;
import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;

public class GuiConfigSlider extends Button
{

    private GuiConfig parent;
    private float sliderValue;
    public boolean dragging;
    private final ConfigProperty property;
    private final float minValue;
    private final float maxValue;
    private final Minecraft mc;

    public GuiConfigSlider(GuiConfig parent, int x, int y, ConfigProperty property, float minValueIn, float maxValue)
    {
        super(x, y, 150, 20, "", (button)->{});
        this.parent = parent;
        this.sliderValue = 1.0F;
        this.property = property;
        this.minValue = minValueIn;
        this.maxValue = maxValue;
        this.sliderValue = (
                minValue + ClientProxy.getConfig().get(property).getAsInt()
                        * 1 / maxValue);
        setMessage("Volume: " + ClientProxy.getConfig().get(property).getAsInt() + "%");

        this.mc = Minecraft.getInstance();
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        this.visible = true;
        super.renderButton(mouseX, mouseY, partialTicks);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (this.visible && parent.canChangeVolume())
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(p_mouseDragged_1_ - (this.x + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                float f = sliderValue;
                //ClientProxy.getConfig().set(this.property, new JsonPrimitive((int) this.sliderValue));

                this.sliderValue = sliderValue;

                int val = (int) (minValue + Helpers.crossMult(sliderValue, 1, maxValue));
                setMessage("Volume: "+val + "%");

                //this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }

            mc.getTextureManager().bindTexture(Widget.WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            //this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            blit(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            //this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            blit(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.parent.canChangeVolume() && super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
        {
            this.sliderValue = (float)(p_mouseClicked_1_ - (this.x + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        this.dragging = false;
        if(!this.parent.canChangeVolume())
        {
            return false;
        }
        int val = (int) (minValue + Helpers.crossMult(sliderValue, 1, maxValue));
        setMessage("Volume: "+val + "%");
        if(property == ClientConfig.MICROPHONE_VOLUME)
        {
            MicroManager.getHandler().setVolume(val);
        }
        else if(property == ClientConfig.SPEAKER_VOLUME)
        {
            SpeakerManager.getHandler().setVolume(val);
        }
        else
        {
            ClientProxy.getConfig().set(this.property, new JsonPrimitive(val));
        }
        return true;
    }
}