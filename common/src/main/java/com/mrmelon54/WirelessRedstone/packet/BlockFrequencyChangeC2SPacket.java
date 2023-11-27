package com.mrmelon54.WirelessRedstone.packet;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.block.WirelessTransmitterBlock;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessFrequencyBlockEntity;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessReceiverBlockEntity;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessTransmitterBlockEntity;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;
import java.util.function.Supplier;

public record BlockFrequencyChangeC2SPacket<T extends WirelessFrequencyBlockEntity<T>>(BlockEntityType<T> beType, BlockPos blockPos, int freq) {
    public static BlockFrequencyChangeC2SPacket<WirelessReceiverBlockEntity> decodeReceiver(FriendlyByteBuf byteBuf) {
        return new BlockFrequencyChangeC2SPacket<>(WirelessRedstone.WIRELESS_RECEIVER_BLOCK_ENTITY, byteBuf.readBlockPos(), byteBuf.readInt());
    }

    public static BlockFrequencyChangeC2SPacket<WirelessTransmitterBlockEntity> decodeTransmitter(FriendlyByteBuf byteBuf) {
        return new BlockFrequencyChangeC2SPacket<>(WirelessRedstone.WIRELESS_TRANSMITTER_BLOCK_ENTITY, byteBuf.readBlockPos(), byteBuf.readInt());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeBlockPos(blockPos);
        byteBuf.writeInt(freq);
    }

    public void apply(Supplier<NetworkManager.PacketContext> packetContextSupplier) {
        Player player = packetContextSupplier.get().getPlayer();
        Level level = player.level();
        Optional<T> blockEntity = level.getBlockEntity(blockPos, beType);
        blockEntity.ifPresent(x -> x.setFrequency(freq));
        level.getBlockEntity(blockPos, WirelessRedstone.WIRELESS_TRANSMITTER_BLOCK_ENTITY);
        level.blockEntityChanged(blockPos);
        if (player.containerMenu instanceof WirelessFrequencyContainerMenu wirelessFrequencyContainerMenu) {
            wirelessFrequencyContainerMenu.setData(0, freq);
        }
    }
}
