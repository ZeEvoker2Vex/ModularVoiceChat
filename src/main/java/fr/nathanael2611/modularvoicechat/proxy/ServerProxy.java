package fr.nathanael2611.modularvoicechat.proxy;

import fr.nathanael2611.modularvoicechat.server.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ServerProxy extends CommonProxy
{

    @Override
    public void onSetup(FMLCommonSetupEvent event)
    {
        super.onSetup(event);

        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }
}
