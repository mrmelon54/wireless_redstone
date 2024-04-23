package com.mrmelon54.WirelessRedstone.util;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.infrastructury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class NetworkingConstants {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(WirelessRedstone.MOD_ID, "networking_channel"));
}
