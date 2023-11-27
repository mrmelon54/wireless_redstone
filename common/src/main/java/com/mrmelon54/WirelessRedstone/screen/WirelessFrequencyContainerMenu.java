package com.mrmelon54.WirelessRedstone.screen;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WirelessFrequencyContainerMenu extends AbstractContainerMenu implements ContainerListener {
    private long freq;

    public WirelessFrequencyContainerMenu(int syncId, Inventory inventory, ContainerLevelAccess aNull) {
        super(WirelessRedstone.WIRELESS_FREQUENCY_SCREEN, syncId);
        addSlotListener(this);
    }

    public WirelessFrequencyContainerMenu(int syncId, Inventory inventory, Player player) {
        super(WirelessRedstone.WIRELESS_FREQUENCY_SCREEN, syncId);
        addSlotListener(this);
    }


    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
    }

    @Override
    public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int j) {
        if (i == 1) freq = j;
    }
}
