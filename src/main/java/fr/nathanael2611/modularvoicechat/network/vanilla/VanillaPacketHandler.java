package fr.nathanael2611.modularvoicechat.network.vanilla;

import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.network.commands.PacketListMuted;
import fr.nathanael2611.modularvoicechat.network.commands.PacketLocalMute;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Vanilla packet handler, that use the Minecraft Network system
 */
public class VanillaPacketHandler
{

    /**
     * Define the mod-network, we will use it to send packets ! :3
     */
    private SimpleChannel network;

    /* Simply the protocol version for packets */
    private static final String PROTOCOL_VERSION = "1";

    /* Packet handler instance */
    private static VanillaPacketHandler instance;

    /* Used for automatic registry */
    private int nextID = 0;

    /**
     * Simply the instance getter
     * @return the PacketHandler instance
     */
    public static VanillaPacketHandler getInstance()
    {
        if(instance == null) instance = new VanillaPacketHandler();
        return instance;
    }

    /**
     * Network getter
     * @return SimpleNetworkWrapper instance
     */
    public SimpleChannel getNetwork()
    {
        return network;
    }

    /**
     * This method will register all our packets.
     */
    public void registerPackets()
    {
        this.network = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ModularVoiceChat.MOD_NAME.toLowerCase(), "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        registerPacket(PacketConnectVoice.class, PacketConnectVoice::encode, PacketConnectVoice::decode, PacketConnectVoice::handle);
        registerPacket(PacketLocalMute.class, PacketLocalMute::encode, PacketLocalMute::decode, PacketLocalMute::handle);
        registerPacket(PacketListMuted.class, PacketListMuted::encode, PacketListMuted::decode, PacketListMuted::handle);
    }

    /**
     * Register a single packet
     */
    private <MSG> void registerPacket(Class packetClass, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer)
    {
        network.registerMessage(nextID, packetClass, encoder, decoder, consumer);
        nextID++;
    }

}
