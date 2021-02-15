package fr.nathanael2611.modularvoicechat.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.nathanael2611.modularvoicechat.config.MutedPlayers;
import fr.nathanael2611.modularvoicechat.network.commands.PacketListMuted;
import fr.nathanael2611.modularvoicechat.network.commands.PacketLocalMute;
import fr.nathanael2611.modularvoicechat.network.vanilla.VanillaPacketHandler;
import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import fr.nathanael2611.modularvoicechat.proxy.ServerProxy;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class VoiceMute
{
    public static void register(CommandDispatcher<CommandSource> dispatcher, boolean local){
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder;
        if(local){
            literalargumentbuilder = Commands.literal("localvoicemute")
                    .requires((sender) -> sender.hasPermissionLevel(0));
        }
        else {
             literalargumentbuilder = Commands.literal("voicemute")
                    .requires((sender) -> sender.hasPermissionLevel(2));
        }

        MutedPlayers mutedPlayers = literalargumentbuilder.getLiteral().startsWith("local") ? ClientProxy.getMutedPlayers() : ServerProxy.getMutedPlayers();

        literalargumentbuilder
                .then(Commands.literal("mute")
                    .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        try {
                            String mutedPlayer = EntityArgument.getPlayer(ctx, "player").getName().getString();
                            ServerPlayerEntity sender = ctx.getSource().asPlayer();
                            sender.sendMessage(new TranslationTextComponent("mvc.moderation.mute.playerMuted", mutedPlayer));
                            if(local){
                                VanillaPacketHandler.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> sender), new PacketLocalMute(mutedPlayer, true));
                            }
                            else {
                                mutedPlayers.setMute(mutedPlayer, true);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        return 0;
                })))
                .then(Commands.literal("unmute")
                    .then(Commands.argument("player", EntityArgument.players())
                    .executes(ctx -> {
                        String mutedPlayer = EntityArgument.getPlayer(ctx, "player").getName().getString();
                        ServerPlayerEntity sender = ctx.getSource().asPlayer();
                        sender.sendMessage(new TranslationTextComponent("mvc.moderation.mute.playerUnmuted", mutedPlayer));
                        if(local){
                            VanillaPacketHandler.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> sender), new PacketLocalMute(mutedPlayer, false));
                        }
                        else {
                            mutedPlayers.setMute(mutedPlayer, false);
                        }
                        return 0;
                })))
                .then(Commands.literal("list")
                .executes(ctx -> {
                    ServerPlayerEntity sender = ctx.getSource().asPlayer();
                    sender.sendMessage(new TranslationTextComponent("mvc.moderation.mute.mutedPlayers"));
                    if(local){
                        VanillaPacketHandler.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> sender), new PacketListMuted('a'));
                    }
                    else {
                        for (String name : mutedPlayers.getNames()) {
                            sender.sendMessage(new TranslationTextComponent("§8 - §7" + name));
                        }
                    }
                    return 0;
                }));
        dispatcher.register(literalargumentbuilder);
    }
}