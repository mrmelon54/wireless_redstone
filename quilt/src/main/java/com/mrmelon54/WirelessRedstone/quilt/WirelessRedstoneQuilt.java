package com.mrmelon54.WirelessRedstone.quilt;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class WirelessRedstoneQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        WirelessRedstone.init();
    }
}
