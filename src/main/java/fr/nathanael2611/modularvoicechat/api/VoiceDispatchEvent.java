package fr.nathanael2611.modularvoicechat.api;

import com.google.common.collect.Maps;
import fr.nathanael2611.modularvoicechat.network.objects.VoiceToClient;
import fr.nathanael2611.modularvoicechat.server.VoiceServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;

/**
 * Event used to able the modders to create their own VoiceDispatcher.
 */
public class VoiceDispatchEvent extends PlayerEvent
{

    /* This list will contain all packets waiting to be sent when the finalize() method will be called */
    private HashMap<ServerPlayerEntity, VoiceToClient> hearList = Maps.newHashMap();

    /* The speaker (sender of the audio-data) */
    private ServerPlayerEntity speaker;
    /* The VoiceServer that received the audio-data */
    private VoiceServer voiceServer;
    /* encoded audio-data */
    private byte[] voiceData;
    /* the VoiceProperties */
    private VoiceProperties properties;

    /**
     * Constructor
     * @param server VoiceServer that received audio-data
     * @param speaker Sender that the audio-data come from
     * @param voiceData Encoded voice-data
     */
    public VoiceDispatchEvent(VoiceServer server, ServerPlayerEntity speaker, byte[] voiceData, VoiceProperties properties)
    {
        super(speaker);
        this.speaker = speaker;
        this.voiceServer = server;
        this.voiceData = voiceData;
        this.properties = properties;
    }

    /**
     * Simply the speaker getter
     * @return the speaker
     */
    public ServerPlayerEntity getSpeaker()
    {
        return speaker;
    }

    /**
     * This event is cancelable.
     * @return true
     */
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    /**
     * Simply the VoiceServer getter
     * @return the voice-server
     */
    public VoiceServer getVoiceServer()
    {
        return voiceServer;
    }

    public VoiceProperties getProperties()
    {
        return properties;
    }

    public void setProperties(VoiceProperties properties)
    {
        this.properties = properties;
    }

    /**
     * Simply return a new voice-packet for the given volume
     * @param volume the volume that we want the audio-data to be played
     * @param properties the VoiceProperties that we want to send with the audio-data
     * @return the created packet
     */
    private VoiceToClient getPacket(int volume, VoiceProperties properties)
    {
        return new VoiceToClient(this.getEntityPlayer().getEntityId(), this.voiceData, volume, properties);
    }

    /**
     * Simply return a new voice-packet for the given volume
     * @param volume the volume that we want the audio-data to be played
     * @return the created packet
     */
    private VoiceToClient getPacket(int volume)
    {
        return getPacket(volume, VoiceProperties.empty());
    }

    /**
     * Send voice-data to a specific player, with default volume
     * @param playerMP the player that we want to send the audio-data
     */
    public void dispatchTo(ServerPlayerEntity playerMP)
    {
        this.dispatchTo(playerMP, 100);
    }

    /**
     * Send voice-data to a specific player, with custom volume
     * @param playerMP the player that we want to send the audio-data
     * @param voiceVolume the custom volume that we want the audio-data to be played
     */
    public void dispatchTo(ServerPlayerEntity playerMP, int voiceVolume)
    {
        this.dispatchTo(playerMP, voiceVolume, VoiceProperties.empty());
    }

    /**
     * Send voice-data to a specific player, with custom volume and properties
     * @param playerMP the player that we want to send the audio-data
     * @param voiceVolume the custom volume that we want the audio-data to be played
     * @param properties the VoiceProperties that we want to send with the audio-data
     */
    public void dispatchTo(ServerPlayerEntity playerMP, int voiceVolume, VoiceProperties properties)
    {
        this.dispatchTo(playerMP, voiceVolume, properties, false);
    }

    /**
     * Send voice-data to a specific player, with custom volume, properties, and override
     * @param playerMP the player that we want to send the audio-data
     * @param voiceVolume the custom volume that we want the audio-data to be played
     * @param properties the VoiceProperties that we want to send with the audio-data
     * @param forceOverride if the audio-data still overwrites the old one
     */
    public void dispatchTo(ServerPlayerEntity playerMP, int voiceVolume, VoiceProperties properties, boolean forceOverride)
    {
        VoiceToClient packet = getPacket(voiceVolume, properties);
        if(!forceOverride && this.hearList.containsKey(playerMP))
        {
            if(voiceVolume < this.hearList.get(playerMP).volumePercent)
            {
                return;
            }
        }
        this.hearList.put(playerMP, packet);
    }

    /**
     * Send audio-data to all players, except the speaker, with the default volume
     */
    public void dispatchToAllExceptSpeaker()
    {
        dispatchToAllExceptSpeaker(100);
    }

    /**
     * Send audio-data to all player, except a specific one
     * @param voiceVolume the custom volume that we want the audio-data to be played
     */
    public void dispatchToAllExceptSpeaker(int voiceVolume)
    {
        this.dispatchToAllExceptSpeaker(voiceVolume, VoiceProperties.empty());
    }

    /**
     * Send audio-data to all player, except a specific one
     * @param voiceVolume the custom volume that we want the audio-data to be played
     * @param properties the VoiceProperties that we want to send with audio-data
     */
    public void dispatchToAllExceptSpeaker(int voiceVolume, VoiceProperties properties)
    {
        this.dispatchToAllExceptSpeaker(voiceVolume, properties, false);
    }

    /**
     * Send audio-data to all player, except a specific one
     * @param voiceVolume the custom volume that we want the audio-data to be played
     * @param properties the VoiceProperties that we want to send with audio-data
     * @param forceOverride if the audio-data still overwrites the old one
     */
    public void dispatchToAllExceptSpeaker(int voiceVolume, VoiceProperties properties, boolean forceOverride)
    {
        for (ServerPlayerEntity connectedPlayer : this.voiceServer.getConnectedPlayers())
        {
            this.dispatchTo(connectedPlayer, voiceVolume, properties, forceOverride);
        }
    }

    /**
     * Just finalize the dispatching. Called after the event.
     * Do not use in the event!
     */
    public void finalizeDispatch()
    {
        this.hearList.forEach((player, packet)-> this.voiceServer.send(player, packet));
    }

}