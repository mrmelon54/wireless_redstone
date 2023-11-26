package com.mrmelon54.WirelessRedstone.fabric;

import com.mrmelon54.WirelessRedstone.fabriclike.WirelessRedstoneFabricLike;
import net.fabricmc.api.ModInitializer;

public class WirelessRedstoneFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WirelessRedstoneFabricLike.init();
    }
}
