package com.mrmelon54.WirelessRedstone.util;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.item.WirelessHandheldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HandheldItemUtils {
    public static void addHandheldFromPlayer(ServerPlayer player, ServerLevel world) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack stack = inv.items.get(i);
            if (!stack.isEmpty()) addHandheldEntry(stack, world);
        }
        WirelessRedstone.sendTickScheduleToReceivers(world);
    }

    public static void removeHandheldFromPlayer(ServerPlayer player, ServerLevel world) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack stack = inv.items.get(i);
            if (!stack.isEmpty()) removeHandheldEntry(stack, world);
        }
        WirelessRedstone.sendTickScheduleToReceivers(world);
    }

    public static void addHandheldFromChunk(@Nullable ServerLevel level, ChunkAccess chunk) {
        chunk.getBlockEntitiesPos().forEach(blockPos -> {
            BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
            if (blockEntity instanceof RandomizableContainerBlockEntity containerBlock) {
                CompoundTag compoundTag = containerBlock.getUpdateTag();
                ListTag items = compoundTag.getList("Items", Tag.TAG_COMPOUND);
                items.forEach(tag -> {
                    if (tag instanceof CompoundTag tag1) {
                        ItemStack itemStack = ItemStack.of(tag1);
                        if (itemStack.isEmpty()) return;
                        addHandheldEntry(itemStack, level);
                    }
                });
            }
        });
    }

    public static void removeHandheldFromChunk(ServerLevel level, ChunkAccess chunk) {
        chunk.getBlockEntitiesPos().forEach(blockPos -> {
            BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
            if (blockEntity instanceof RandomizableContainerBlockEntity containerBlock) {
                CompoundTag compoundTag = containerBlock.getUpdateTag();
                ListTag items = compoundTag.getList("Items", Tag.TAG_COMPOUND);
                items.forEach(tag -> {
                    if (tag instanceof CompoundTag tag1) {
                        ItemStack itemStack = ItemStack.of(tag1);
                        if (itemStack.isEmpty()) return;
                        removeHandheldEntry(itemStack, level);
                    }
                });
            }
        });
    }

    public static void addHandheldEntry(ItemStack stack, ServerLevel level) {
        if (!stack.is(WirelessRedstone.WIRELESS_HANDHELD)) return;

        CompoundTag compoundTag = stack.getOrCreateTag();
        boolean enabled = compoundTag.getBoolean(WirelessHandheldItem.WIRELESS_HANDHELD_ENABLED);
        if (!enabled) return;

        UUID uuid = compoundTag.getUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID);
        int freq = compoundTag.getInt(WirelessHandheldItem.WIRELESS_HANDHELD_FREQ);
        WirelessRedstone.getWirelessHandheld(level.dimension()).add(new TransmittingHandheldEntry(uuid, freq));
    }

    public static void removeHandheldEntry(ItemStack stack, ServerLevel level) {
        if (!stack.is(WirelessRedstone.WIRELESS_HANDHELD)) return;

        CompoundTag compound = WirelessHandheldItem.getOrCreateNbt(stack);
        UUID uuid = compound.getUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID);
        WirelessRedstone.getWirelessHandheld(level.dimension()).removeIf(transmittingHandheldEntry -> transmittingHandheldEntry.handheldUuid().equals(uuid));
    }
}
