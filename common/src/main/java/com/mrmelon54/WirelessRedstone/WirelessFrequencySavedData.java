package com.mrmelon54.WirelessRedstone;

import com.mrmelon54.WirelessRedstone.util.TransmittingFrequencyEntry;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WirelessFrequencySavedData extends SavedData implements WirelessFrequencyStorage {
    private final Set<BlockPos> receivers = new HashSet<>();
    private final Set<TransmittingFrequencyEntry> transmitting = new HashSet<>();
    private final Set<TransmittingHandheldEntry> handheld = new HashSet<>();

    public WirelessFrequencySavedData() {
    }

    public WirelessFrequencySavedData(CompoundTag compoundTag) {
        ListTag receivers = compoundTag.getList("wireless_receivers", Tag.TAG_COMPOUND);
        for (Tag item : receivers)
            if (item instanceof CompoundTag compound) {
                int x = compound.getInt("x");
                int y = compound.getInt("y");
                int z = compound.getInt("z");
                this.receivers.add(new BlockPos(x, y, z));
            }

        ListTag transmitting = compoundTag.getList("wireless_transmitting", Tag.TAG_COMPOUND);
        for (Tag item : transmitting)
            if (item instanceof CompoundTag compound) {
                int x = compound.getInt("x");
                int y = compound.getInt("y");
                int z = compound.getInt("z");
                long freq = compound.getLong("freq");
                this.transmitting.add(new TransmittingFrequencyEntry(new BlockPos(x, y, z), freq));
            }

        ListTag handheld = compoundTag.getList("wireless_handheld", Tag.TAG_COMPOUND);
        for (Tag item : handheld)
            if (item instanceof CompoundTag compound) {
                UUID uuid = compound.getUUID("uuid");
                long freq = compound.getLong("freq");
                this.handheld.add(new TransmittingHandheldEntry(uuid, freq));
            }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        ListTag receiversList = new ListTag();
        for (BlockPos pos : receivers) {
            CompoundTag compound = new CompoundTag();
            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());
            receiversList.add(compound);
        }
        compoundTag.put("wireless_receivers", receiversList);

        ListTag transmittingList = new ListTag();
        for (TransmittingFrequencyEntry entry : transmitting) {
            CompoundTag compound = new CompoundTag();
            compound.putInt("x", entry.blockPos().getX());
            compound.putInt("y", entry.blockPos().getY());
            compound.putInt("z", entry.blockPos().getZ());
            compound.putLong("freq", entry.freq());
            transmittingList.add(compound);
        }
        compoundTag.put("wireless_transmitting", transmittingList);

        ListTag handheldList = new ListTag();
        for (TransmittingHandheldEntry entry : handheld) {
            CompoundTag compound = new CompoundTag();
            compound.putUUID("uuid", entry.handheldUuid());
            compound.putLong("freq", entry.freq());
            handheldList.add(compound);
        }
        compoundTag.put("wireless_handheld", handheldList);
        return compoundTag;
    }

    @Override
    public Set<BlockPos> getReceivers() {
        return receivers;
    }

    @Override
    public Set<TransmittingFrequencyEntry> getTransmitting() {
        return transmitting;
    }

    @Override
    public Set<TransmittingHandheldEntry> getHandheld() {
        return handheld;
    }

    public static Factory<WirelessFrequencySavedData> factory() {
        return new Factory<>(WirelessFrequencySavedData::new, WirelessFrequencySavedData::new, DataFixTypes.LEVEL);
    }
}
