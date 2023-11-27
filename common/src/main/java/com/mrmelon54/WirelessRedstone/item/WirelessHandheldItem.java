package com.mrmelon54.WirelessRedstone.item;

import com.mrmelon54.WirelessRedstone.WirelessFrequencySavedData;
import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WirelessHandheldItem extends Item implements MenuProvider {
    public static final String WIRELESS_HANDHELD_UUID = "wireless_handheld_uuid";
    public static final String WIRELESS_HANDHELD_ENABLED = "wireless_handheld_enabled";
    public static final String WIRELESS_HANDHELD_FREQ = "wireless_handheld_freq";

    public WirelessHandheldItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.EAT;
    }

    @Override
    public boolean useOnRelease(ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.isCrouching()) {
            player.openMenu(this);
            return InteractionResultHolder.pass(itemStack);
        }

        CompoundTag compoundTag = getOrCreateNbt(itemStack);
        if (compoundTag == null) return InteractionResultHolder.fail(itemStack);

        boolean v = !compoundTag.getBoolean(WIRELESS_HANDHELD_ENABLED);
        compoundTag.putBoolean(WIRELESS_HANDHELD_ENABLED, v);
        int freq = compoundTag.getInt(WIRELESS_HANDHELD_FREQ);

        WirelessFrequencySavedData dim = WirelessRedstone.getDimensionSavedData(level);

        UUID uuid = compoundTag.getUUID(WIRELESS_HANDHELD_UUID);
        dim.getHandheld().removeIf(x -> {
            System.out.println(x.handheldUuid().toString() + " compared to " + uuid);
            return x.handheldUuid().equals(uuid);
        });

        if (v) {
            UUID uuid2 = UUID.randomUUID();
            compoundTag.putUUID(WIRELESS_HANDHELD_UUID, uuid);
            dim.getHandheld().add(new TransmittingHandheldEntry(uuid, freq));
        }
        dim.setDirty();

        WirelessRedstone.sendTickScheduleToReceivers(level);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);

        CompoundTag compoundTag = getOrCreateNbt(itemStack);
        if (compoundTag == null) return;

        boolean enabled = compoundTag.getBoolean(WIRELESS_HANDHELD_ENABLED);
        int freq = compoundTag.getInt(WIRELESS_HANDHELD_FREQ);
        MutableComponent freqTooltip = Component.translatable("item.wireless_redstone.item.tooltip-frequency", freq);
        list.add(enabled ? freqTooltip.withStyle(ChatFormatting.GREEN) : freqTooltip.withStyle(ChatFormatting.RED));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new WirelessFrequencyContainerMenu(i, new ContainerData() {
            @Override
            public int get(int i) {
                ItemStack stackInHand = player.getMainHandItem();
                CompoundTag compound = WirelessHandheldItem.getOrCreateNbt(stackInHand);
                if (compound == null) return 0;
                return compound.getInt(WIRELESS_HANDHELD_FREQ);
            }

            @Override
            public void set(int i, int j) {
                ItemStack stackInHand = player.getMainHandItem();
                CompoundTag compound = WirelessHandheldItem.getOrCreateNbt(stackInHand);
                if (compound == null) return;

                boolean enabled = compound.getBoolean(WirelessHandheldItem.WIRELESS_HANDHELD_ENABLED);
                compound.putInt(WirelessHandheldItem.WIRELESS_HANDHELD_FREQ, i);

                // remove old transmit signal and replace it
                Set<TransmittingHandheldEntry> handheld = WirelessRedstone.getDimensionSavedData(player.level()).getHandheld();
                if (enabled) {
                    UUID uuid = compound.getUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID);
                    handheld.removeIf(x -> x.handheldUuid().equals(uuid));

                    UUID uuid1 = UUID.randomUUID();
                    compound.putUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID, uuid1);
                    handheld.add(new TransmittingHandheldEntry(uuid1, i));
                }

                WirelessRedstone.sendTickScheduleToReceivers(player.level());
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    public static CompoundTag getOrCreateNbt(ItemStack itemStack) {
        if (itemStack == null) return null;

        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (!itemStack.is(WirelessRedstone.WIRELESS_HANDHELD)) return compoundTag;

        if (!compoundTag.hasUUID(WirelessHandheldItem.WIRELESS_HANDHELD_UUID))
            compoundTag.putUUID(WIRELESS_HANDHELD_UUID, UUID.randomUUID());
        if (!compoundTag.contains(WIRELESS_HANDHELD_ENABLED, Tag.TAG_BYTE))
            compoundTag.putBoolean(WIRELESS_HANDHELD_ENABLED, false);
        if (!compoundTag.contains(WIRELESS_HANDHELD_FREQ, Tag.TAG_INT))
            compoundTag.putInt(WIRELESS_HANDHELD_FREQ, 0);
        return compoundTag;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        super.onDestroyed(itemEntity);

        CompoundTag compoundTag = getOrCreateNbt(itemEntity.getItem());
        if (compoundTag == null) return;

        UUID uuid = compoundTag.getUUID(WIRELESS_HANDHELD_UUID);
        compoundTag.putBoolean(WIRELESS_HANDHELD_ENABLED, false);

        WirelessFrequencySavedData dim = WirelessRedstone.getDimensionSavedData(itemEntity.level());
        dim.getHandheld().removeIf(transmittingHandheldEntry -> transmittingHandheldEntry.handheldUuid().equals(uuid));
        dim.setDirty();

        WirelessRedstone.sendTickScheduleToReceivers(itemEntity.level());
    }
}
