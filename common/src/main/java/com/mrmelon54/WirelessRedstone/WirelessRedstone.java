package com.mrmelon54.WirelessRedstone;

import com.google.common.base.Suppliers;
import com.mrmelon54.WirelessRedstone.block.WirelessReceiverBlock;
import com.mrmelon54.WirelessRedstone.block.WirelessTransmitterBlock;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessReceiverBlockEntity;
import com.mrmelon54.WirelessRedstone.block.entity.WirelessTransmitterBlockEntity;
import com.mrmelon54.WirelessRedstone.item.WirelessHandheldItem;
import com.mrmelon54.WirelessRedstone.menu.WirelessFrequencyMenu;
import com.mrmelon54.WirelessRedstone.models.HandheldModelProvider;
import com.mrmelon54.WirelessRedstone.packet.BlockFrequencyChangeC2SPacket;
import com.mrmelon54.WirelessRedstone.packet.HandheldFrequencyChangeC2SPacket;
import com.mrmelon54.WirelessRedstone.screen.WirelessFrequencyScreen;
import com.mrmelon54.WirelessRedstone.util.HandheldItemUtils;
import com.mrmelon54.WirelessRedstone.util.NetworkingConstants;
import com.mrmelon54.infrastructury.Infrastructury;
import com.mrmelon54.infrastructury.event.events.client.ClientLifecycleEvent;
import com.mrmelon54.infrastructury.event.events.common.ChunkEvent;
import com.mrmelon54.infrastructury.event.events.common.LifecycleEvent;
import com.mrmelon54.infrastructury.event.events.common.PlayerEvent;
import com.mrmelon54.infrastructury.registry.CreativeTabRegistry;
import com.mrmelon54.infrastructury.registry.item.ItemPropertiesRegistry;
import com.mrmelon54.infrastructury.registry.menu.MenuRegistry;
import com.mrmelon54.infrastructury.registry.registries.Registrar;
import com.mrmelon54.infrastructury.registry.registries.RegistrarManager;
import com.mrmelon54.infrastructury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screens.Screen;

public class WirelessRedstone {
    public static final String MOD_ID = "wireless_redstone";
    public static ConfigStructure CONFIG = AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new).get();
    public static final Logger LOGGER = LoggerFactory.getLogger(WirelessRedstone.class);

    public static final Block WIRELESS_TRANSMITTER = new WirelessTransmitterBlock(BlockBehaviour.Properties.of().strength(0).lightLevel(litFrequencyBlockEmission()).sound(SoundType.METAL));
    public static final BlockItem WIRELESS_TRANSMITTER_ITEM = new BlockItem(WIRELESS_TRANSMITTER, new Item.Properties().stacksTo(64));
    public static final Block WIRELESS_RECEIVER = new WirelessReceiverBlock(BlockBehaviour.Properties.of().strength(0).lightLevel(litFrequencyBlockEmission()).sound(SoundType.METAL));
    public static final BlockItem WIRELESS_RECEIVER_ITEM = new BlockItem(WIRELESS_RECEIVER, new Item.Properties().stacksTo(64));
    public static final Item WIRELESS_HANDHELD = new WirelessHandheldItem(new Item.Properties().stacksTo(1));
    public static BlockEntityType<WirelessTransmitterBlockEntity> WIRELESS_TRANSMITTER_BLOCK_ENTITY;
    public static BlockEntityType<WirelessReceiverBlockEntity> WIRELESS_RECEIVER_BLOCK_ENTITY;
    public static RegistrySupplier<MenuType<WirelessFrequencyMenu>> WIRELESS_FREQUENCY_MENU;

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    private static final Map<ResourceKey<Level>, WirelessFrequencySavedData> levelData = new HashMap<>();

    public static WirelessFrequencySavedData getDimensionSavedData(Level level) {
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

        CreativeTabRegistry.append(CreativeModeTabs.REDSTONE_BLOCKS, WIRELESS_RECEIVER_ITEM, WIRELESS_TRANSMITTER_ITEM, WIRELESS_HANDHELD);

        Registrar<MenuType<?>> menuReg = MANAGER.get().get(Registries.MENU);
        Registrar<Block> blockReg = MANAGER.get().get(Registries.BLOCK);
        Registrar<Item> itemReg = MANAGER.get().get(Registries.ITEM);
        Registrar<BlockEntityType<?>> blockEntityReg = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

        blockReg.register(new ResourceLocation(MOD_ID, "transmitter"), () -> WIRELESS_TRANSMITTER);
        itemReg.register(new ResourceLocation(MOD_ID, "transmitter"), () -> WIRELESS_TRANSMITTER_ITEM);
        WIRELESS_TRANSMITTER_BLOCK_ENTITY = BlockEntityType.Builder.of(WirelessTransmitterBlockEntity::new, WIRELESS_TRANSMITTER).build(null);
        blockEntityReg.register(new ResourceLocation(MOD_ID, "transmitter"), (Supplier<BlockEntityType<?>>) () -> WIRELESS_TRANSMITTER_BLOCK_ENTITY);

        blockReg.register(new ResourceLocation(MOD_ID, "receiver"), () -> WIRELESS_RECEIVER);
        itemReg.register(new ResourceLocation(MOD_ID, "receiver"), () -> WIRELESS_RECEIVER_ITEM);
        WIRELESS_RECEIVER_BLOCK_ENTITY = BlockEntityType.Builder.of(WirelessReceiverBlockEntity::new, WIRELESS_RECEIVER).build(null);
        blockEntityReg.register(new ResourceLocation(MOD_ID, "receiver"), (Supplier<BlockEntityType<?>>) () -> WIRELESS_RECEIVER_BLOCK_ENTITY);

        itemReg.register(new ResourceLocation(MOD_ID, "handheld"), () -> WIRELESS_HANDHELD);

        WIRELESS_FREQUENCY_MENU = menuReg.register(new ResourceLocation(MOD_ID, "set_frequency_menu"), () -> MenuRegistry.ofExtended(WirelessFrequencyMenu::new));

        NetworkingConstants.CHANNEL.register(BlockFrequencyChangeC2SPacket.class, BlockFrequencyChangeC2SPacket::encode, BlockFrequencyChangeC2SPacket::decode, BlockFrequencyChangeC2SPacket::apply);
        NetworkingConstants.CHANNEL.register(HandheldFrequencyChangeC2SPacket.class, HandheldFrequencyChangeC2SPacket::encode, HandheldFrequencyChangeC2SPacket::decode, HandheldFrequencyChangeC2SPacket::apply);

        LifecycleEvent.SERVER_LEVEL_LOAD.register(world -> {
            DimensionDataStorage dataStorage = world.getDataStorage();
            WirelessFrequencySavedData savedData = dataStorage.computeIfAbsent(WirelessFrequencySavedData.factory(), MOD_ID);
            levelData.put(world.dimension(), savedData);
            dataStorage.set(MOD_ID, savedData);
        });
        ClientLifecycleEvent.CLIENT_SETUP.register(instance -> MenuRegistry.registerScreenFactory(WIRELESS_FREQUENCY_MENU.get(), (containerMenu, inventory, component) -> new WirelessFrequencyScreen(containerMenu)));

        clientInit();
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        ItemPropertiesRegistry.register(WirelessRedstone.WIRELESS_HANDHELD, new ResourceLocation("wireless_redstone_handheld_enabled"), new HandheldModelProvider());
        Infrastructury.registerConfigScreen((mc,screen)->createConfigScreen(screen).get());
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

    public static Supplier<Screen> createConfigScreen(Screen screen) {
        return AutoConfig.getConfigScreen(ConfigStructure.class, screen);
    }
}
