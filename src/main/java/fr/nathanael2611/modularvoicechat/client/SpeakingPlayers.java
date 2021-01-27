package fr.nathanael2611.modularvoicechat.client;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

public class SpeakingPlayers
{

    private static HashMap<Integer, Long> SPEAKING_PLAYERS = Maps.newHashMap();

    public static boolean isTalking(PlayerEntity player)
    {
        return System.currentTimeMillis() - getTalkingValue(player) < 200;
    }

    public static void updateTalking(int id)
    {
        SPEAKING_PLAYERS.put(id, System.currentTimeMillis());
    }

    private static long getTalkingValue(PlayerEntity player)
    {
        return SPEAKING_PLAYERS.getOrDefault(player.getEntityId(), 0L);
    }


}
