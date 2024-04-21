package com.mrmelon54.WirelessRedstone.menu;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.packet.BlockFrequencyChangeC2SPacket;
import com.mrmelon54.WirelessRedstone.packet.HandheldFrequencyChangeC2SPacket;
import com.mrmelon54.WirelessRedstone.util.NetworkingConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WirelessFrequencyMenu extends AbstractContainerMenu {
    private final boolean isHandheld;
    private final boolean isReceiver;
    private final BlockPos blockPos;

    public WirelessFrequencyMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(WirelessRedstone.WIRELESS_FREQUENCY_MENU.get(), id);
        isHandheld = buf.readBoolean();
        if (!isHandheld) {
            isReceiver = buf.readBoolean();
            blockPos = buf.readBlockPos();
        } else {
            isReceiver = false;
            blockPos = BlockPos.ZERO;
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void sendResponseMessage(int freq) {
        if (isHandheld) NetworkingConstants.CHANNEL.sendToServer(new HandheldFrequencyChangeC2SPacket(freq));
        else NetworkingConstants.CHANNEL.sendToServer(new BlockFrequencyChangeC2SPacket(isReceiver, blockPos, freq));
    }
}