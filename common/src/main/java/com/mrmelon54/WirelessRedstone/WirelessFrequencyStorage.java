package com.mrmelon54.WirelessRedstone;

import com.mrmelon54.WirelessRedstone.util.TransmittingFrequencyEntry;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import net.minecraft.core.BlockPos;

import java.util.Set;

public interface WirelessFrequencyStorage {
    Set<BlockPos> getReceivers();

    Set<TransmittingFrequencyEntry> getTransmitting();

    Set<TransmittingHandheldEntry> getHandheld();
}
