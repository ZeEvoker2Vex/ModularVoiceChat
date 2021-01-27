package fr.nathanael2611.modularvoicechat.network.vanilla;

import fr.nathanael2611.modularvoicechat.client.ClientEventHandler;
import fr.nathanael2611.modularvoicechat.client.voice.VoiceClientManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.MicroManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.SpeakerManager;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

/**
 * This will be send from the server to the client,
 * for request it to connect to the VoiceServer bind to the given port
 */
public class PacketConnectVoice
{

    private String ip;
    /* VoiceServer port */
    private int port;
    /* Player to link name */
    private String playerName;
    /* Show who speak */
    private boolean showWhoSpeak;

    /**
     * Constructor
     *
     * @param port VoiceServer port
     */
    public PacketConnectVoice(String ip, int port, String playerName, boolean showWhoSpeak)
    {
        this.ip = ip;
        this.port = port;
        this.playerName = playerName;
        this.showWhoSpeak = showWhoSpeak;
    }

    /**
     * Reading packet
     *
     * @param buf buf that contain the packet objects
     */
    public static PacketConnectVoice decode(PacketBuffer buf)
    {
        String ip = buf.readString(255);
        int port = buf.readInt();
        String playerName = buf.readString(255);
        boolean showWhoSpeak = buf.readBoolean();
        return new PacketConnectVoice(ip, port, playerName, showWhoSpeak);
    }

    /**
     * Writing packet to ByteBuf
     *
     * @param buf buf to write on
     */
    public static void encode(PacketConnectVoice packet, PacketBuffer buf)
    {
        buf.writeString(packet.ip);
        buf.writeInt(packet.port);
        buf.writeString(packet.playerName);
        buf.writeBoolean(packet.showWhoSpeak);
    }

    public static void handle(PacketConnectVoice packet, Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            if (ctx.get().getNetworkManager().getRemoteAddress() instanceof InetSocketAddress) {
                InetSocketAddress address = (InetSocketAddress) ctx.get().getNetworkManager().getRemoteAddress();
                String host = packet.ip.length() > 0 ? packet.ip : getHostName(address);
                Helpers.log("Receiving voice-connect packet from server: " + host);
                new Thread(() ->
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Helpers.log("Connected to a Minecraft Server, trying to handle voice connection.");
                    if (VoiceClientManager.isStarted()) VoiceClientManager.stop();
                    if (MicroManager.isRunning()) MicroManager.stop();
                    if (SpeakerManager.isRunning()) SpeakerManager.stop();
                    Helpers.log("[pre] Handle VoiceClient start.");
                    VoiceClientManager.start(packet.playerName, host, packet.port);
                    MicroManager.start();
                    SpeakerManager.start();
                    ClientEventHandler.showWhoSpeak = packet.showWhoSpeak;
                }).start();

            }
        }
        ctx.get().setPacketHandled(true);
    }

    public static String getHostName(InetSocketAddress rescue)
    {
        ServerData serverData = Minecraft.getInstance().getCurrentServerData();
        if(serverData != null)
        {
            return serverData.serverIP.split(":")[0];
        }
        else
        {
            String host = rescue.getHostName();
            if(host.endsWith("."))
            {
                host = host.substring(0, host.length() - 1);
            }
            return host;
        }
    }

}
