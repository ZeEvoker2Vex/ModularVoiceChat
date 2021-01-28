package fr.nathanael2611.modularvoicechat.proxy;

import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.config.MutedPlayers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.io.File;

public class CommonProxy
{
    private static MutedPlayers mutedPlayers;

    public void onSetup(FMLCommonSetupEvent event)
    {
        mutedPlayers = new MutedPlayers(new File(ModularVoiceChat.modConfigDir, "MutedPlayers.json"));
    }

    public static MutedPlayers getMutedPlayers()
    {
        return mutedPlayers;
    }
}
