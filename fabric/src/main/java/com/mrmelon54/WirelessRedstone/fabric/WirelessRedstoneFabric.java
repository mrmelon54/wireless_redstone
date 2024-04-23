package com.mrmelon54.WirelessRedstone.fabric;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.fabricmc.api.ModInitializer;

public class WirelessRedstoneFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WirelessRedstone.init();
    }
}
