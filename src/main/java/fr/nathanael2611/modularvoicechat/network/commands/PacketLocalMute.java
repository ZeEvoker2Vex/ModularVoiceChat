package fr.nathanael2611.modularvoicechat.network.commands;

import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLocalMute {

    /* Name of muted player */
    private String playerName;
    /* Mute status of player */
    private boolean muted;

    /**
     * Constructor
     *
     * @param playerName Name of muted player
     * @param muted Mute status of player
     */
    public PacketLocalMute(String playerName, boolean muted)
    {
        this.playerName = playerName;
        this.muted = muted;
    }

    /**
     * Reading packet
     *
     * @param buf buf that contain the packet objects
     */
    public static PacketLocalMute decode(PacketBuffer buf)
    {
        String playerName = buf.readString(255);
        boolean muted = buf.readBoolean();
        return new PacketLocalMute(playerName, muted);
    }

    /**
     * Writing packet to ByteBuf
     *
     * @param buf buf to write on
     */
    public static void encode(PacketLocalMute packet, PacketBuffer buf)
    {
        buf.writeString(packet.playerName);
        buf.writeBoolean(packet.muted);
    }

    public static void handle(PacketLocalMute packet, Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            ClientProxy.getMutedPlayers().setMute(packet.playerName, packet.muted);
        }
        ctx.get().setPacketHandled(true);
    }
}
