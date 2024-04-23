package com.mrmelon54.WirelessRedstone.packet;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.item.WirelessHandheldItem;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import com.mrmelon54.infrastructury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.UUID;
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

        ItemStack stackInHand = player.getMainHandItem();
        CompoundTag compound = WirelessHandheldItem.getOrCreateNbt(stackInHand);
        if (compound == null) return;

        boolean enabled = compound.getBoolean(WirelessHandheldItem.WIRELESS_HANDHELD_ENABLED);
        compound.putInt(WirelessHandheldItem.WIRELESS_HANDHELD_FREQ, freq);

        Set<TransmittingHandheldEntry> handheld = WirelessRedstone.getDimensionSavedData(player.level()).getHandheld();

        UUID uuid = compound.getUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID);
        handheld.removeIf(x -> x.handheldUuid().equals(uuid));

        if (enabled) {
            UUID uuid1 = UUID.randomUUID();
            compound.putUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID, uuid1);
            handheld.add(new TransmittingHandheldEntry(uuid1, freq));
        }

        WirelessRedstone.sendTickScheduleToReceivers(player.level());

    }
}
