package com.mrmelon54.WirelessRedstone.forge;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WirelessRedstone.MOD_ID)
public class WirelessRedstoneForge {
    public WirelessRedstoneForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(WirelessRedstone.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        WirelessRedstone.init();
    }
}
