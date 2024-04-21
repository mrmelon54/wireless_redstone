package com.mrmelon54.WirelessRedstone.screen;

import com.mrmelon54.WirelessRedstone.menu.WirelessFrequencyMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WirelessFrequencyScreen extends Screen implements MenuAccess<WirelessFrequencyMenu> {
    private static final ResourceLocation MENU_LOCATION = new ResourceLocation("wireless_redstone:textures/gui/frequency.png");
    private final WirelessFrequencyMenu containerMenu;
    private final Inventory inventory;
    private EditBox freqBox;

    public WirelessFrequencyScreen(WirelessFrequencyMenu containerMenu, Inventory inventory, Component component) {
        super(Component.translatable("screen.wireless_redstone.set_frequency"));
        this.containerMenu = containerMenu;
        this.inventory = inventory;
        init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderBackground(guiGraphics, i, j, f);
        super.render(guiGraphics, i, j, f);
        freqBox.render(guiGraphics, i, j, f);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.blit(MENU_LOCATION, (width - 176) / 2, (height - 50) / 2, 0, 0, 176, 50);
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - 176) / 2;
        int j = (this.height - 50) / 2;
        freqBox = new EditBox(this.font, i + 62, j + 24, 103, 12, Component.translatable("container.repair"));
        freqBox.setCanLoseFocus(false);
        freqBox.setTextColor(-1);
        freqBox.setTextColorUneditable(-1);
        freqBox.setBordered(false);
        freqBox.setMaxLength(50);
        freqBox.setFilter(s -> {
            if (s.isEmpty()) return false;
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    }
                    default -> {
                        return false;
                    }
                }
            }
            return true;
        });
        freqBox.setFormatter((s, integer) -> {
            if (Objects.equals(s, "")) s = "0";
            long freq = Integer.parseInt(s);
            return FormattedCharSequence.forward(String.valueOf(freq), Style.EMPTY);
        });
        freqBox.setResponder(this::onFreqChanged);
        freqBox.setValue("0");
        this.addWidget(freqBox);
        this.setInitialFocus(freqBox);
    }

    private void onFreqChanged(String s) {
        int freq = 0;
        if (!Objects.equals(s, ""))
            freq = Integer.parseInt(s);
        containerMenu.sendResponseMessage(freq);
    }

    @Override
    public @NotNull WirelessFrequencyMenu getMenu() {
        return containerMenu;
    }
}
