package com.mrmelon54.WirelessRedstone.forge;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WirelessRedstone.MOD_ID)
public class WirelessRedstoneForge {
    public WirelessRedstoneForge() {
        EventBuses.registerModEventBus(WirelessRedstone.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        WirelessRedstone.init();
    }
}
