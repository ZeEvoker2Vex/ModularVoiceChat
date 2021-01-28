package fr.nathanael2611.modularvoicechat.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.collect.BiMap;
import fr.nathanael2611.modularvoicechat.api.VoiceDispatchEvent;
import fr.nathanael2611.modularvoicechat.api.VoiceProperties;
import fr.nathanael2611.modularvoicechat.network.objects.*;
import fr.nathanael2611.modularvoicechat.proxy.CommonProxy;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;

public class KryoNetServerListener extends Listener
{

    private VoiceServer voiceServer;

    KryoNetServerListener(VoiceServer server)
    {
        this.voiceServer = server;
    }

    @Override
    public void disconnected(Connection connection)
    {
        super.disconnected(connection);
        BiMap<Connection, Integer> map = this.voiceServer.CONNECTIONS_MAP.inverse();
        if(map.containsKey(connection))
        {
            this.voiceServer.CONNECTIONS_MAP.remove(map.get(connection));
        }
    }

    @Override
    public void received(Connection connection, Object object)
    {
        ServerPlayerEntity player = this.voiceServer.getPlayer(connection);
        if (object instanceof HelloImAPlayer)
        {
            HelloImAPlayer hello = ((HelloImAPlayer) object);
            Helpers.log("A new player tried to connect to VoiceServer named: " + hello.playerName);
            ServerPlayerEntity playerMP = Helpers.getPlayerByUsername(hello.playerName);
            if (playerMP != null)
            {
                voiceServer.CONNECTIONS_MAP.remove(playerMP.getEntityId());
                voiceServer.CONNECTIONS_MAP.put(playerMP.getEntityId(), connection);
                Helpers.log("Successfully added " + hello.playerName + " to voice-server connected-players!");
                connection.sendTCP(new HelloYouAreAPlayer());
            }
            else
            {
                Helpers.log("No player named: " + hello.playerName);
            }
        } else if (player != null)
        {
            if (object instanceof VoiceToServer)
            {
                if(!CommonProxy.getMutedPlayers().isMuted(player.getName().getString()))
                {
                    VoiceToServer voiceToServer = (VoiceToServer) object;
                    VoiceDispatchEvent event = new VoiceDispatchEvent(voiceServer, player, voiceToServer.opusBytes, VoiceProperties.empty());
                    MinecraftForge.EVENT_BUS.post(event);
                    if(!event.isCanceled())
                    {
                        event.getVoiceServer().getVoiceDispatcher().dispatch(event);
                    }
                    event.finalizeDispatch();
                }
                else
                {
                    STitlePacket spackettitle1 = new STitlePacket(STitlePacket.Type.ACTIONBAR,
                            new TranslationTextComponent("mvc.error.muted"),
                            1, 1, 1);
                    player.connection.sendPacket(spackettitle1);
                }
            }
            else if (object instanceof VoiceEndToServer)
            {
                this.voiceServer.sendToAllExcept(player, new VoiceEndToClient(player.getEntityId()));
            }
        }
        super.received(connection, object);
    }
}