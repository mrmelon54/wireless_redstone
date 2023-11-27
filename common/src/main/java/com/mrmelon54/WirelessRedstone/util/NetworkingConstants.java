package com.mrmelon54.WirelessRedstone.util;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class NetworkingConstants {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(WirelessRedstone.MOD_ID, "networking_channel"));

    // Packet IDs
    public static final ResourceLocation WIRELESS_FREQUENCY_CHANGE_PACKET_ID = new ResourceLocation(WirelessRedstone.MOD_ID, "wireless_frequency_change");
}
