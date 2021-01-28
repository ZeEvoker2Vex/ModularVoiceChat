package fr.nathanael2611.modularvoicechat.network.commands;

import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketListMuted {

    /* Necessary variable for the packet */
    private char c;

    /**
     * Constructor
     *
     * @param c Needed param to do the packet functional
     */
    public PacketListMuted(char c)
    {
    }

    /**
     * Reading packet
     *
     * @param buf buf that contain the packet objects
     */
    public static PacketListMuted decode(PacketBuffer buf)
    {
        return new PacketListMuted('a');
    }

    /**
     * Writing packet to ByteBuf
     *
     * @param buf buf to write on
     */
    public static void encode(PacketListMuted packet, PacketBuffer buf)
    {
    }

    public static void handle(PacketListMuted packet, Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            listPlayers();
        }
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void listPlayers(){
        PlayerEntity player = Minecraft.getInstance().player;
        for (String name : ClientProxy.getMutedPlayers().getNames()) {
            player.sendMessage(new TranslationTextComponent("§8 - §7" + name));
        }
    }
}
