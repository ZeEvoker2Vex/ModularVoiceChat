package fr.nathanael2611.modularvoicechat.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class HearDistanceEvent extends Event
{

    private ServerPlayerEntity speaker;
    private ServerPlayerEntity hearer;
    private double distance;

    public HearDistanceEvent(ServerPlayerEntity speaker, ServerPlayerEntity hearer, double distance)
    {
        this.speaker = speaker;
        this.hearer = hearer;
        this.distance = distance;
    }

    public ServerPlayerEntity getSpeaker()
    {
        return speaker;
    }

    public ServerPlayerEntity getHearer()
    {
        return hearer;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    @Override
    public boolean isCancelable()
    {
        return false;
    }
}
