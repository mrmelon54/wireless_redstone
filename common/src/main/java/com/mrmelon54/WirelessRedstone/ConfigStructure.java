package com.mrmelon54.WirelessRedstone;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wireless_redstone")
@Config.Gui.Background("minecraft:textures/block/dirt.png")
public class ConfigStructure implements ConfigData {
    public boolean modeEnabled = true;
}
