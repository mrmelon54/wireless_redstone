package com.mrmelon54.WirelessRedstone.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public record HandheldFrequencyChangeC2SPacket(int freq) {
    public static HandheldFrequencyChangeC2SPacket decode(FriendlyByteBuf byteBuf) {
        return new HandheldFrequencyChangeC2SPacket(byteBuf.readInt());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(freq);
    }

    public void apply(Supplier<NetworkManager.PacketContext> packetContextSupplier) {
        Player player = packetContextSupplier.get().getPlayer();

        if (player.containerMenu instanceof WirelessFrequencyContainerMenu wirelessFrequencyContainerMenu) {
            wirelessFrequencyContainerMenu.setData(0, freq);
        }
    }
}
