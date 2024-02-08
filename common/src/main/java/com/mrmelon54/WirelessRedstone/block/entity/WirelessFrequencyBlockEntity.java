package com.mrmelon54.WirelessRedstone.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessFrequencyBlockEntity<T extends WirelessFrequencyBlockEntity<T>> extends BlockEntity {
    private int frequency;

    public WirelessFrequencyBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
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

    public void setFrequency(int frequency) {
        this.frequency = frequency;
        setChanged();
    }
}
