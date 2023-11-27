package com.mrmelon54.WirelessRedstone.fabriclike;

import com.mrmelon54.WirelessRedstone.WirelessFrequencyStorage;
import com.mrmelon54.WirelessRedstone.util.TransmittingFrequencyEntry;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import net.minecraft.core.BlockPos;

import java.util.Set;

public class WorldStorage implements WirelessFrequencyStorage {
    @Override
    public Set<BlockPos> getReceivers() {
        return null;
    }

    @Override
    public Set<TransmittingFrequencyEntry> getTransmitting() {
        return null;
    }

    @Override
    public Set<TransmittingHandheldEntry> getHandheld() {
        return null;
    }
}
