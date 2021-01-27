package fr.nathanael2611.modularvoicechat.config;

import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.api.dispatcher.IVoiceDispatcher;
import fr.nathanael2611.modularvoicechat.server.dispatcher.DistanceBasedVoiceDispatcher;
import fr.nathanael2611.modularvoicechat.server.dispatcher.GlobalVoiceDispatcher;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

//@Config(modid = ModularVoiceChat.MOD_ID, name = ModularVoiceChat.MOD_NAME + "/ServerConfig")
@Mod.EventBusSubscriber
public class ServerConfig
{
    public static final String CATEGORY_GENERAL = "generalconfig";
    public static final String SUBCATEGORY_DISPATCHER = "dispatcher";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static General generalConfig;

    static {

        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("This is the general config of ModularVoiceChat").push(CATEGORY_GENERAL);
        generalConfig = new General(SERVER_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class General
    {
        public static ForgeConfigSpec.ConfigValue<String> forcedHostname;
        public static ForgeConfigSpec.IntValue port;
        public static ForgeConfigSpec.BooleanValue showWhoSpeak;
        public static Dispatcher dispatcher;

        public General(ForgeConfigSpec.Builder SERVER_BUILDER){

            port = SERVER_BUILDER.comment("The vocal-server port").defineInRange("port", ModularVoiceChat.DEFAULT_PORT, 0, Integer.MAX_VALUE);
            showWhoSpeak = SERVER_BUILDER.comment("Define if the micro on the speaking-players will be rendered.").define("showWhoSpeak", false);
            forcedHostname = SERVER_BUILDER.comment("This field is optionnal, but may correct some issue with connecting to voice-server!", "By providing an given hostname you are assured that all players use the same.")
                    .define("forcedHostname", "");

            SERVER_BUILDER.comment("The used voice-dispatcher").push(SUBCATEGORY_DISPATCHER);
            dispatcher = new Dispatcher(SERVER_BUILDER);

            SERVER_BUILDER.pop();
        }
    }

    public static class Dispatcher
    {
        public static ForgeConfigSpec.ConfigValue<String> dispatchType;
        public static ForgeConfigSpec.IntValue maxDistance;
        public static ForgeConfigSpec.BooleanValue fadeOut;

        public Dispatcher(ForgeConfigSpec.Builder SERVER_BUILDER){

            dispatchType = SERVER_BUILDER.comment("The DispatchType", " - \"distanced\" for a distance-based voice-dispatch", " - \"global\" for a global, to all players, voice-dispatch")
                    .define("dispatchType", "distanced");
            maxDistance = SERVER_BUILDER.comment("If DispatchType is \"distanced\", it will be the max-distance that a player can hear another one.")
                    .defineInRange("maxDistance", 15, 0, Integer.MAX_VALUE);
            fadeOut = SERVER_BUILDER.comment("If DispatchType is \"distanced\":", "If true, the sound will fade-out with the distance")
                    .define("fadeOut", true);

            SERVER_BUILDER.pop();
        }

        public IVoiceDispatcher createDispatcher()
        {
            if(this.dispatchType.get().equalsIgnoreCase("global"))
            {
                return new GlobalVoiceDispatcher();
            }
            else
            {
                return new DistanceBasedVoiceDispatcher(this.maxDistance.get(), this.fadeOut.get());
            }
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }
    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
    }
}
