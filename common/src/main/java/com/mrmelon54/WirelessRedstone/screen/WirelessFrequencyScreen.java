package com.mrmelon54.WirelessRedstone.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class WirelessFrequencyScreen extends Screen implements MenuAccess<WirelessFrequencyContainerMenu> {
    private final WirelessFrequencyContainerMenu menu;

    public WirelessFrequencyScreen(WirelessFrequencyContainerMenu menu, Player player, Component title) {
        super(Component.translatable("screen.wireless_redstone.set_frequency"));
        this.menu = menu;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(Minecraft.getInstance().font, "Hello", 0, 0, 0);
    }

    @Override
    public @NotNull WirelessFrequencyContainerMenu getMenu() {
        return menu;
    }
}
