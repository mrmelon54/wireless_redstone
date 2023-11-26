package com.mrmelon54.WirelessRedstone.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public record WirelessFrequencyChangeC2SPacket(int freq) implements Packet<ServerGamePacketListener> {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(freq);
    }

    @Override
    public void handle(ServerGamePacketListener packetListener) {
    }
}
