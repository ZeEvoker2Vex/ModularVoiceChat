package fr.nathanael2611.modularvoicechat.client.gui;

import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.GlStateManager;
import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.audio.AudioTester;
import fr.nathanael2611.modularvoicechat.audio.micro.MicroData;
import fr.nathanael2611.modularvoicechat.audio.speaker.SpeakerData;
import fr.nathanael2611.modularvoicechat.client.ClientEventHandler;
import fr.nathanael2611.modularvoicechat.client.voice.audio.MicroManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.SpeakerManager;
import fr.nathanael2611.modularvoicechat.config.ClientConfig;
import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import fr.nathanael2611.modularvoicechat.util.AudioUtil;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URI;

public class GuiConfig extends Screen
{

    public static boolean audioTesting = false;

    private ClientConfig config;
    private Minecraft mc;

    public GuiConfig()
    {
        super(new StringTextComponent("gui."+ModularVoiceChat.MOD_ID+".config"));
        this.config = ClientProxy.getConfig();
        this.mc = Minecraft.getInstance();
    }

    private GuiDropDownMenu microSelector;
    private GuiDropDownMenu speakerSelector;
    private GuiConfigSlider microVolume;
    private GuiConfigSlider speakerVolume;
    private Button toggleToTalk;
    private Button audioTest;

    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);

        buttons.clear();
        audioTesting = false;
        AudioTester.updateTester();
        int y = 80 + 20;

        Button.IPressable actionToggleToTalk = button -> {
            if(!this.speakerSelector.dropDownMenu && !this.microSelector.dropDownMenu) {
                this.config.set(ClientConfig.TOGGLE_TO_TALK, new JsonPrimitive(!this.config.get(ClientConfig.TOGGLE_TO_TALK).getAsBoolean()));
                button.setMessage("Mode: " + getSpeakMode());
            }
        };
        Button.IPressable actionAudioTest = button -> {
            if(!this.speakerSelector.dropDownMenu && !this.microSelector.dropDownMenu) {
                audioTesting = !audioTesting;

                button.setMessage((audioTesting ? I18n.format("mvc.config.audio.test.on") : I18n.format("mvc.config.audio.test.off")));
                AudioTester.updateTester();
            }
        };
        Button.IPressable actionDiscordButton = button -> {
            if(!this.speakerSelector.dropDownMenu && !this.microSelector.dropDownMenu) {
                try {
                    Desktop.getDesktop().browse(new URI(ModularVoiceChat.DISCORD_INVITE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Button.IPressable actionMicroSelector = button -> {
            GuiDropDownMenu drop = (GuiDropDownMenu) button;
            MicroManager.getHandler().setMicro(this.microSelector.getSelectedText());

            if(drop.dropDownMenu) drop.setMessage(drop.getSelectedText());
            drop.dropDownMenu = !drop.dropDownMenu;
        };
        Button.IPressable actionSpeakerSelector = button -> {
            GuiDropDownMenu drop = (GuiDropDownMenu) button;
            SpeakerManager.getHandler().setSpeaker(this.speakerSelector.getSelectedText());

            if(drop.dropDownMenu) drop.setMessage(drop.getSelectedText());
            drop.dropDownMenu = !drop.dropDownMenu;
        };

        this.addButton(this.microVolume = new GuiConfigSlider(this,width / 2 - 150 - 5, y + 25, ClientConfig.MICROPHONE_VOLUME, 0, 150));
        this.addButton(this.speakerVolume = new GuiConfigSlider(this, width/ 2 + 5, y + 25, ClientConfig.SPEAKER_VOLUME, 0, 150));
        this.addButton(this.toggleToTalk = new Button(width / 2 - 150 - 5, y + 50, 150, 20, "Mode: " + getSpeakMode(), actionToggleToTalk));
        this.addButton(this.audioTest = new Button(width / 2 + 5, y + 50, 150, 20, (audioTesting ? I18n.format("mvc.config.audio.test.on") : I18n.format("mvc.config.audio.test.off")), actionAudioTest));
        this.addButton(new Button(width / 2 - 155, y + 50 + 25,150 + 5 + 5 + 150, 20, I18n.format("mvc.config.joindiscord"), actionDiscordButton));
        this.addButton(this.microSelector = new GuiDropDownMenu(width / 2 - 150 - 4, y, 148, 20, MicroManager.getHandler().getMicro(), Helpers.getStringListAsArray(AudioUtil.findAudioDevices(MicroData.MIC_INFO)), actionMicroSelector));
        this.addButton(this.speakerSelector = new GuiDropDownMenu(width / 2 + 6, y, 148, 20, SpeakerManager.getHandler().getSpeaker(), Helpers.getStringListAsArray(AudioUtil.findAudioDevices(SpeakerData.SPEAKER_INFO)), actionSpeakerSelector));

    }

    public String getSpeakMode()
    {
        return this.config.get(ClientConfig.TOGGLE_TO_TALK).getAsBoolean() ? I18n.format("mvc.config.toggletotalk") : I18n.format("mvc.config.pushtotalk");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        GlStateManager.pushMatrix();
        GlStateManager.color3f(1, 1, 1);
        GlStateManager.translatef((width / 2) - 32 - (mc.fontRenderer.getStringWidth(ModularVoiceChat.MOD_NAME) / 2), 30, 1);
        GlStateManager.scaled(1.2, 1.2, 1.2);
        mc.getTextureManager().bindTexture(ClientEventHandler.MICRO);
        // int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight
        //Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 32, 32, 32, 32);
        blit(0, 0, 0, 0, 32, 32, 32, 32);

        mc.fontRenderer.drawStringWithShadow(ModularVoiceChat.MOD_NAME, 32, 10, Color.WHITE.getRGB());
        GlStateManager.popMatrix();
        mc.fontRenderer.drawStringWithShadow("§n" + I18n.format("mvc.config.audio.input"), width / 2 - 150 - 5, 80, Color.WHITE.getRGB());
        mc.fontRenderer.drawStringWithShadow("§n" + I18n.format("mvc.config.audio.output"), width / 2 + 5, 80, Color.WHITE.getRGB());
        mc.fontRenderer.drawStringWithShadow("§7" + I18n.format("mvc.config.canMute"), width / 2 - 150 - 5, 200, Color.WHITE.getRGB());

        super.render(mouseX, mouseY, partialTicks);
        if(mouseX > this.microSelector.x && mouseX < this.microSelector.x + this.microSelector.getWidth() &&
                mouseY > this.microSelector.y && mouseY < this.microSelector.y + this.microSelector.getHeight() &&
                !this.microSelector.dropDownMenu)
        {
            this.font.drawString(I18n.format("mvc.config.audio.output.desc"), mouseX, mouseY, 1);
        }
        else if(mouseX > this.speakerSelector.x && mouseX < this.speakerSelector.x + this.speakerSelector.getWidth() &&
                mouseY > this.speakerSelector.y && mouseY < this.speakerSelector.y + this.speakerSelector.getHeight() &&
                !this.speakerSelector.dropDownMenu)
        {
            this.font.drawString(I18n.format("mvc.config.audio.input.desc"), mouseX, mouseY, 1);
        }
        else if(mouseX > this.microVolume.x && mouseX < this.microVolume.x + this.microVolume.getWidth() &&
                mouseY > this.microVolume.y && mouseY < this.microVolume.y + this.microVolume.getHeight())
        {
            this.font.drawString(I18n.format("mvc.config.audio.input.volume"), mouseX, mouseY, 1);
        }
        else if(mouseX > this.speakerVolume.x && mouseX < this.speakerVolume.x + this.speakerVolume.getWidth() &&
                mouseY > this.speakerVolume.y && mouseY < this.speakerVolume.y + this.speakerVolume.getHeight())
        {
            this.font.drawString(I18n.format("mvc.config.audio.output.volume"), mouseX, mouseY, 1);
        }
    }

    public boolean canChangeVolume()
    {
        return !(this.microSelector.isMouseOver() || this.speakerSelector.isMouseOver());
    }

    @Override
    public void onClose()
    {
        audioTesting = false;
        AudioTester.updateTester();
        super.onClose();
    }
}
