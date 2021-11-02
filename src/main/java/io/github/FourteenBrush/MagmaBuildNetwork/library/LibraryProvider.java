package io.github.FourteenBrush.MagmaBuildNetwork.library;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;

public abstract class LibraryProvider {

    protected static final Main plugin = Main.getPlugin(Main.class);
    protected static final ConfigManager configManager = plugin.getConfigManager();
}
