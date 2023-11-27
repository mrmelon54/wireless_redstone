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
import dev.architectury.event.events.common.ChunkEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
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
import net.minecraft.world.level.storage.LevelResource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
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

    private static final Map<ResourceKey<Level>, WirelessFrequencySavedData> levelData = new HashMap<>();

    public static WirelessFrequencySavedData getDimensionSavedData(Level level) {
        System.out.println("getDimensionSavedData(" + level.dimension() + ")");
        return levelData.getOrDefault(level.dimension(), new WirelessFrequencySavedData(new CompoundTag()));
    }

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

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkingConstants.WIRELESS_FREQUENCY_CHANGE_PACKET_ID, (buf, context) -> {
            if (context.getPlayer().containerMenu instanceof WirelessFrequencyContainerMenu wirelessFrequencyContainerMenu) {
                wirelessFrequencyContainerMenu.setData(0, buf.readInt());
            }
        });

        LifecycleEvent.SERVER_LEVEL_LOAD.register(new LifecycleEvent.ServerLevelState() {
            @Override
            public void act(ServerLevel world) {
                System.out.println("=== Level load event ===");
                System.out.println("Level path: " + world.getServer().getWorldPath(LevelResource.ROOT));
                System.out.println("Is client side: " + world.isClientSide);
                WirelessFrequencySavedData savedData = world.getDataStorage().get(WirelessFrequencySavedData::new, MOD_ID);
                levelData.put(world.dimension(), savedData);
            }
        });

        LifecycleEvent.SERVER_LEVEL_SAVE.register(new LifecycleEvent.ServerLevelState() {
            @Override
            public void act(ServerLevel world) {
                System.out.println("=== Level save event ===");
                System.out.println("Level path: " + world.getServer().getWorldPath(LevelResource.ROOT));
                System.out.println("Is client side: " + world.isClientSide);
                levelData.forEach(new BiConsumer<ResourceKey<Level>, WirelessFrequencySavedData>() {
                    @Override
                    public void accept(ResourceKey<Level> levelResourceKey, WirelessFrequencySavedData wirelessFrequencySavedData) {
                        world.getDataStorage().set(MOD_ID, wirelessFrequencySavedData);
                    }
                });
            }
        });

        clientInit();
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        //noinspection Convert2Lambda
        MenuRegistry.registerScreenFactory(WirelessRedstone.WIRELESS_FREQUENCY_SCREEN, new MenuRegistry.ScreenFactory<WirelessFrequencyContainerMenu, WirelessFrequencyScreen>() {
            @Override
            public WirelessFrequencyScreen create(WirelessFrequencyContainerMenu containerMenu, Inventory inventory, Component component) {
                return new WirelessFrequencyScreen(containerMenu, inventory.player, component);
            }
        });
    }

    public static void sendTickScheduleToReceivers(Level level) {
        getDimensionSavedData(level).getReceivers().forEach(blockPos -> level.scheduleTick(blockPos, WIRELESS_RECEIVER, 0));
    }

    public static boolean hasLitTransmitterOnFrequency(Level level, long frequency) {
        return getDimensionSavedData(level).getTransmitting().stream().anyMatch(x -> x.freq() == frequency)
                || getDimensionSavedData(level).getHandheld().stream().anyMatch(x -> x.freq() == frequency);
    }

    private static ToIntFunction<BlockState> litFrequencyBlockEmission() {
        return (blockState) -> blockState.getValue(BlockStateProperties.LIT) ? 7 : 0;
    }
}
