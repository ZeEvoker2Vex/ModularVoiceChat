package fr.nathanael2611.modularvoicechat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import fr.nathanael2611.modularvoicechat.config.ServerConfig;
import fr.nathanael2611.modularvoicechat.network.vanilla.VanillaPacketHandler;
import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import fr.nathanael2611.modularvoicechat.proxy.CommonProxy;
import fr.nathanael2611.modularvoicechat.proxy.ServerProxy;
import fr.nathanael2611.modularvoicechat.server.VoiceServerManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(ModularVoiceChat.MOD_ID)
public class ModularVoiceChat
{

    public static final String MOD_ID = "modularvc";
    public static final String MOD_NAME = "ModularVoiceChat";

/*    @Mod.Instance(MOD_ID)
    public static ModularVoiceChat INSTANCE;*/

    private static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static File modConfigDir;

    public static final int DEFAULT_PORT = 7656;
    public static final String DISCORD_INVITE = "https://discord.gg/kSu7eFE";

    public ModularVoiceChat() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStop);

        try {
            File serverConfigFile = new File(FMLPaths.CONFIGDIR.get().toFile(), ModularVoiceChat.MOD_NAME + "/ServerConfig.toml");
            if (!serverConfigFile.getParentFile().exists()) serverConfigFile.getParentFile().mkdirs();
            if (!serverConfigFile.exists()) serverConfigFile.createNewFile();
            CommentedFileConfig commentedFileConfig = CommentedFileConfig.builder(serverConfigFile).sync().autosave().writingMode(WritingMode.REPLACE).build();
            ServerConfig.SERVER_CONFIG.setConfig(commentedFileConfig);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onSetup(FMLCommonSetupEvent event)
    {
        VanillaPacketHandler.getInstance().registerPackets();

        proxy.onSetup(event);
    }

    public void onServerStart(FMLServerStartingEvent event)
    {
        if(event.getServer().isDedicatedServer())
        {
            if(!VoiceServerManager.isStarted())
            {
                VoiceServerManager.start();
            }
        }
    }

    public void onServerStop(FMLServerStoppingEvent event)
    {
        if(VoiceServerManager.isStarted())
        {
            VoiceServerManager.stop();
        }
    }

}
