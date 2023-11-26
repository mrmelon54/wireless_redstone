package com.mrmelon54.WirelessRedstone.models;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.item.WirelessHandheldItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HandheldModelProvider implements ClampedItemPropertyFunction {
    @Override
    public float unclampedCall(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
        if (itemStack == null || itemStack.isEmpty() || !itemStack.is(WirelessRedstone.WIRELESS_HANDHELD)) return -1;
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        return compoundTag.getBoolean(WirelessHandheldItem.WIRELESS_HANDHELD_ENABLED) ? 0.01f : 0;
    }
}
