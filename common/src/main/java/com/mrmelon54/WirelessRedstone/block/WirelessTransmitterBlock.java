package com.mrmelon54.WirelessRedstone.block;

import com.mojang.serialization.MapCodec;
import com.mrmelon54.WirelessRedstone.WirelessFrequencySavedData;
import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessTransmitterBlockEntity;
import com.mrmelon54.WirelessRedstone.util.TransmittingFrequencyEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

public class WirelessTransmitterBlock extends WirelessFrequencyBlock {
    public static final MapCodec<WirelessTransmitterBlock> CODEC = simpleCodec(WirelessTransmitterBlock::new);

    public WirelessTransmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        updateWirelessTransmittingState(blockState, level, blockPos);
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        updateWirelessTransmittingState(blockState, level, blockPos);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.hasBlockEntity() && !blockState.is(blockState2.getBlock())) {
            level.removeBlockEntity(blockPos);
            updateWirelessFrequency(level, blockPos, false, 0);
        }
    }

    private void updateWirelessTransmittingState(BlockState blockState, Level level, BlockPos blockPos) {
        boolean bl = blockState.getValue(BlockStateProperties.LIT);
        boolean z = level.hasNeighborSignal(blockPos);
        if (bl == z) return;

        level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, z), Block.UPDATE_CLIENTS);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof WirelessTransmitterBlockEntity wirelessTransmitterBlockEntity)
            updateWirelessFrequency(level, blockPos, z, wirelessTransmitterBlockEntity.getFrequency());
        else
            updateWirelessFrequency(level, blockPos, false, 0);
    }

    private void updateWirelessFrequency(Level level, BlockPos blockPos, boolean isPowered, long freq) {
        WirelessFrequencySavedData dim = WirelessRedstone.getDimensionSavedData(level);
        Set<TransmittingFrequencyEntry> transmitting = dim.getTransmitting();
        if (isPowered) transmitting.add(new TransmittingFrequencyEntry(blockPos.immutable(), freq));
        else transmitting.removeIf(x -> x.blockPos().equals(blockPos));
        dim.setDirty();
        WirelessRedstone.sendTickScheduleToReceivers(level);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WirelessTransmitterBlockEntity(blockPos, blockState);
    }
}
