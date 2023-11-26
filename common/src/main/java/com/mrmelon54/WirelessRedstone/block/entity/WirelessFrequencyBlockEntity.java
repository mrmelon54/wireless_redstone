package com.mrmelon54.WirelessRedstone.block.entity;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WirelessFrequencyBlockEntity<T extends WirelessFrequencyBlockEntity<T>> extends BlockEntity implements MenuProvider {
    private int frequency;

    public WirelessFrequencyBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        frequency = 0;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("frequency", Tag.TAG_INT)) frequency = compoundTag.getInt("frequency");
        else frequency = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putInt("frequency", frequency);
    }

    public int getFrequency() {
        return frequency;
    }

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int i) {
            if (i == 0) return getFrequency();
            return -1;
        }

        @Override
        public void set(int i, int j) {
            if (i == 0) {
                frequency = j;
                if (level != null) WirelessRedstone.sendTickScheduleToReceivers(level);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null; // TODO: add menu
    }
}
