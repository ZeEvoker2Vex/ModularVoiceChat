package fr.nathanael2611.modularvoicechat.proxy;

import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.audio.AudioTester;
import fr.nathanael2611.modularvoicechat.client.ClientEventHandler;
import fr.nathanael2611.modularvoicechat.config.ClientConfig;
import fr.nathanael2611.modularvoicechat.util.OpusLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class ClientProxy extends CommonProxy
{

    public static final KeyBinding KEY_SPEAK = new KeyBinding("mvc.config.pushtotalk", KeyEvent.VK_V, ModularVoiceChat.MOD_NAME);
    public static final KeyBinding KEY_OPEN_CONFIG = new KeyBinding("mvc.config.openconfig", InputMappings.INPUT_INVALID.getKeyCode(), ModularVoiceChat.MOD_NAME);

    private static ClientConfig config;

    @Override
    public void onSetup(FMLCommonSetupEvent event)
    {
        super.onSetup(event);

        ClientRegistry.registerKeyBinding(KEY_SPEAK);
        ClientRegistry.registerKeyBinding(KEY_OPEN_CONFIG);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler(Minecraft.getInstance()));

        File clientConfigFile = new File(ModularVoiceChat.modConfigDir, "ClientConfig.json");
        if(!clientConfigFile.exists()) {
            try {
                clientConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = new ClientConfig(clientConfigFile);

        //ClientCommandHandler.instance.registerCommand(new VoiceMuteClient());

        if(!OpusLoader.loadOpus())
        {
            JOptionPane.showMessageDialog(null, "\n" + "Opus initialization failed. ModularVoiceChat will not work.", "Opus initialization error", JOptionPane.ERROR_MESSAGE);
            exitJava(0, true);
        }

        AudioTester.start();
    }

    public static ClientConfig getConfig()
    {
        return config;
    }

    public void exitJava(int exitCode, boolean hardExit)
    {
        Logger logger = LogManager.getLogger(ModularVoiceChat.MOD_ID);
        logger.warn("Java has been asked to exit (code {})", exitCode);
        if (hardExit)
        {
            logger.warn("This is an abortive exit and could cause world corruption or other things");
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        logger.warn("Exit trace:");
        //The first 2 elements are Thread#getStackTrace and FMLCommonHandler#exitJava and aren't relevant
        for (int i = 2; i < stack.length; i++)
        {
            logger.warn("\t{}", stack[i]);
        }
        if (hardExit)
        {
            Runtime.getRuntime().halt(exitCode);
        }
        else
        {
            Runtime.getRuntime().exit(exitCode);
        }
    }
}
