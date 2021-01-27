package fr.nathanael2611.modularvoicechat.server;

import fr.nathanael2611.modularvoicechat.api.VoiceProperties;
import fr.nathanael2611.modularvoicechat.config.ServerConfig;
import fr.nathanael2611.modularvoicechat.network.vanilla.PacketConnectVoice;
import fr.nathanael2611.modularvoicechat.network.vanilla.VanillaPacketHandler;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerEventHandler
{

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (VoiceServerManager.isStarted())
        {
            Helpers.log("Requesting " + event.getPlayer().getName() + " to connect to voice-server... Sending packet.");
            VanillaPacketHandler.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketConnectVoice(ServerConfig.General.forcedHostname.get(), VoiceServerManager.getServer().getPort(), event.getPlayer().getName().getString(), ServerConfig.General.showWhoSpeak.get()));
        }

        System.out.println(VoiceServerManager.getServer().CONNECTIONS_MAP.toString());
    }

}
