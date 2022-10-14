package com.petey.harvest_leveling;

import com.mojang.logging.LogUtils;
import com.petey.harvest_leveling.commands.DumpTiersCommand;
import com.petey.harvest_leveling.config.ModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(HarvestLeveling.MOD_ID)
public class HarvestLeveling {
    public static final String MOD_ID = "harvest_leveling";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static IEventBus modEventBus;
    public HarvestLeveling() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ModConfig.cacheValues();
        modEventBus.register(new ModConfig());
    }

    @SubscribeEvent
    public void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        DumpTiersCommand.register(event.getDispatcher());
    }
}
