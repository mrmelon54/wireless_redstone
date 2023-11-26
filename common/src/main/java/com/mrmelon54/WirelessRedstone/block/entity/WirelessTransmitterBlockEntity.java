package com.mrmelon54.WirelessRedstone.block.entity;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessTransmitterBlockEntity extends WirelessFrequencyBlockEntity<WirelessTransmitterBlockEntity> {
    public WirelessTransmitterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WirelessRedstone.WIRELESS_TRANSMITTER_BLOCK_ENTITY, blockPos, blockState);
    }
}
