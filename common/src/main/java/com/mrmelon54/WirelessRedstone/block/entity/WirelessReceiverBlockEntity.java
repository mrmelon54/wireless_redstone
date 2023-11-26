package com.mrmelon54.WirelessRedstone.block.entity;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WirelessReceiverBlockEntity extends WirelessFrequencyBlockEntity<WirelessReceiverBlockEntity> {
    public WirelessReceiverBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WirelessRedstone.WIRELESS_RECEIVER_BLOCK_ENTITY, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, WirelessReceiverBlockEntity be) {
        if (level.isClientSide) return;

        if (level.getBlockEntity(blockPos) instanceof WirelessReceiverBlockEntity wirelessReceiverBlockEntity) {
            Boolean isLit = blockState.getValue(BlockStateProperties.LIT);
            long frequency = wirelessReceiverBlockEntity.getFrequency();
            boolean shouldBeLit = WirelessRedstone.hasLitTransmitterOnFrequency(level, frequency);
            if (shouldBeLit != isLit)
                level.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, shouldBeLit));
            return;
        }
        level.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, false));
    }
}
