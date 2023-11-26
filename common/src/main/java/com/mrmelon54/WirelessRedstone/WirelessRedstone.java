package com.mrmelon54.WirelessRedstone;

import com.google.common.base.Suppliers;
import com.mrmelon54.WirelessRedstone.block.WirelessTransmitterBlock;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessReceiverBlockEntity;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessTransmitterBlockEntity;
import com.mrmelon54.WirelessRedstone.item.WirelessHandheldItem;
import com.mrmelon54.WirelessRedstone.screen.WirelessFrequencyContainerMenu;
import com.mrmelon54.WirelessRedstone.screen.WirelessFrequencyScreen;
import com.mrmelon54.WirelessRedstone.util.HandheldItemUtils;
import com.mrmelon54.WirelessRedstone.util.NetworkingConstants;
import com.mrmelon54.WirelessRedstone.util.TransmittingFrequencyEntry;
import com.mrmelon54.WirelessRedstone.util.TransmittingHandheldEntry;
import dev.architectury.event.events.common.ChunkEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class WirelessRedstone {
    public static final String MOD_ID = "wireless_redstone";

    public static final Block WIRELESS_TRANSMITTER = new WirelessTransmitterBlock(BlockBehaviour.Properties.of().strength(0).lightLevel(litFrequencyBlockEmission()).sound(SoundType.METAL));
    public static final BlockItem WIRELESS_TRANSMITTER_ITEM = new BlockItem(WIRELESS_TRANSMITTER, new Item.Properties().stacksTo(64));
    public static final Block WIRELESS_RECEIVER = new WirelessTransmitterBlock(BlockBehaviour.Properties.of().strength(0).lightLevel(litFrequencyBlockEmission()).sound(SoundType.METAL));
    public static final BlockItem WIRELESS_RECEIVER_ITEM = new BlockItem(WIRELESS_RECEIVER, new Item.Properties().stacksTo(64));
    public static final Item WIRELESS_HANDHELD = new WirelessHandheldItem(new Item.Properties().stacksTo(1));
    public static BlockEntityType<WirelessTransmitterBlockEntity> WIRELESS_TRANSMITTER_BLOCK_ENTITY;
    public static BlockEntityType<WirelessReceiverBlockEntity> WIRELESS_RECEIVER_BLOCK_ENTITY;
    public static MenuType<WirelessFrequencyContainerMenu> WIRELESS_FREQUENCY_SCREEN;
    public static final BlockPos IMPOSSIBLE_BLOCK_POS = BlockPos.ZERO.below(30000000);

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    // === Loaded entries ===
    private static final Map<ResourceKey<Level>, Set<BlockPos>> wirelessReceivers = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<TransmittingFrequencyEntry>> wirelessTransmitting = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<TransmittingHandheldEntry>> wirelessHandheld = new HashMap<>();

    public static Set<BlockPos> getWirelessReceivers(ResourceKey<Level> levelKey) {
        Set<BlockPos> z = wirelessReceivers.get(levelKey);
        if (z == null) {
            z = new HashSet<>();
            wirelessReceivers.put(levelKey, new HashSet<>());
        }
        return z;
    }

    public static Set<TransmittingFrequencyEntry> getWirelessTransmitting(ResourceKey<Level> levelKey) {
        Set<TransmittingFrequencyEntry> z = wirelessTransmitting.get(levelKey);
        if (z == null) {
            z = new HashSet<>();
            wirelessTransmitting.put(levelKey, new HashSet<>());
        }
        return z;
    }

    public static Set<TransmittingHandheldEntry> getWirelessHandheld(ResourceKey<Level> levelKey) {
        Set<TransmittingHandheldEntry> z = wirelessHandheld.get(levelKey);
        if (z == null) {
            z = new HashSet<>();
            wirelessHandheld.put(levelKey, new HashSet<>());
        }
        return z;
    }
    // === /Loaded entries ===

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> HandheldItemUtils.addHandheldFromPlayer(player, player.serverLevel()));
        PlayerEvent.PLAYER_QUIT.register(player -> HandheldItemUtils.removeHandheldFromPlayer(player, player.serverLevel()));
        ChunkEvent.LOAD_DATA.register((chunk, level, nbt) -> HandheldItemUtils.addHandheldFromChunk(level, chunk));
        ChunkEvent.SAVE_DATA.register((chunk, level, nbt) -> HandheldItemUtils.removeHandheldFromChunk(level, chunk));

        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> {
            HandheldItemUtils.removeHandheldFromPlayer(player, player.server.getLevel(oldLevel));
            HandheldItemUtils.addHandheldFromPlayer(player, player.server.getLevel(newLevel));
        });

        //noinspection UnstableApiUsage
        CreativeTabRegistry.append(CreativeModeTabs.REDSTONE_BLOCKS, WIRELESS_RECEIVER_ITEM, WIRELESS_TRANSMITTER_ITEM, WIRELESS_HANDHELD);

        Registrar<MenuType<?>> menuReg = MANAGER.get().get(Registries.MENU);
        Registrar<Block> blockReg = MANAGER.get().get(Registries.BLOCK);
        Registrar<Item> itemReg = MANAGER.get().get(Registries.ITEM);
        Registrar<BlockEntityType<?>> blockEntityReg = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

        WIRELESS_FREQUENCY_SCREEN = new MenuType<>(((syncId, inventory) -> new WirelessFrequencyContainerMenu(syncId, inventory, ContainerLevelAccess.NULL)), FeatureFlagSet.of());
        menuReg.register(new ResourceLocation(MOD_ID, "frequency_screen"), () -> WIRELESS_FREQUENCY_SCREEN);

        blockReg.register(new ResourceLocation(MOD_ID, "transmitter"), () -> WIRELESS_TRANSMITTER);
        itemReg.register(new ResourceLocation(MOD_ID, "transmitter"), () -> WIRELESS_TRANSMITTER_ITEM);
        WIRELESS_TRANSMITTER_BLOCK_ENTITY = BlockEntityType.Builder.of(WirelessTransmitterBlockEntity::new, WIRELESS_TRANSMITTER).build(null);
        blockEntityReg.register(new ResourceLocation(MOD_ID, "transmitter"), (Supplier<BlockEntityType<?>>) () -> WIRELESS_TRANSMITTER_BLOCK_ENTITY);

        blockReg.register(new ResourceLocation(MOD_ID, "receiver"), () -> WIRELESS_RECEIVER);
        itemReg.register(new ResourceLocation(MOD_ID, "receiver"), () -> WIRELESS_RECEIVER_ITEM);
        WIRELESS_RECEIVER_BLOCK_ENTITY = BlockEntityType.Builder.of(WirelessReceiverBlockEntity::new, WIRELESS_RECEIVER).build(null);
        blockEntityReg.register(new ResourceLocation(MOD_ID, "receiver"), (Supplier<BlockEntityType<?>>) () -> WIRELESS_RECEIVER_BLOCK_ENTITY);

        itemReg.register(new ResourceLocation(MOD_ID, "handheld"), () -> WIRELESS_HANDHELD);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkingConstants.WIRELESS_FREQUENCY_CHANGE_PACKET_ID, new NetworkManager.NetworkReceiver() {
            @Override
            public void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
                if (context.getPlayer().containerMenu instanceof WirelessFrequencyContainerMenu wirelessFrequencyContainerMenu) {

                }
            }
        });

        clientInit();
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        dev.architectury.registry.menu.MenuRegistry.registerScreenFactory(WirelessRedstone.WIRELESS_FREQUENCY_SCREEN, (gui, inventory, title) -> new WirelessFrequencyScreen(gui, inventory.player, title));
    }

    public static void sendTickScheduleToReceivers(Level level) {
        WirelessRedstone.getWirelessReceivers(level.dimension()).forEach(blockPos -> level.scheduleTick(blockPos, WirelessRedstone.WIRELESS_RECEIVER, 0));
    }

    public static boolean hasLitTransmitterOnFrequency(Level level, long frequency) {
        return WirelessRedstone.getWirelessTransmitting(level.dimension()).stream().anyMatch(x -> x.freq() == frequency)
                || WirelessRedstone.getWirelessHandheld(level.dimension()).stream().anyMatch(x -> x.freq() == frequency);
    }

    private static ToIntFunction<BlockState> litFrequencyBlockEmission() {
        return (blockState) -> blockState.getValue(BlockStateProperties.LIT) ? 7 : 0;
    }
}
