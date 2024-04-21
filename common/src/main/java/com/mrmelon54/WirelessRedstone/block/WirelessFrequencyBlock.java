package com.mrmelon54.WirelessRedstone.block;

import com.mrmelon54.WirelessRedstone.WirelessRedstone;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class WirelessFrequencyBlock extends BaseEntityBlock {
    protected WirelessFrequencyBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, new ExtendedMenuProvider() {
                @Override
                public void saveExtraData(FriendlyByteBuf buf) {
                    buf.writeBoolean(false); // is handheld
                    buf.writeBoolean(blockState.getBlock() == WirelessRedstone.WIRELESS_RECEIVER);
                    buf.writeBlockPos(blockPos);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.wireless_redstone.set_frequency");
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return WirelessRedstone.WIRELESS_FREQUENCY_MENU.get().create(i, inventory);
                }
            });
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
    }
}
