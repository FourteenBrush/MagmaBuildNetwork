package io.github.FourteenBrush.MagmaBuildNetwork.library;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;

public abstract class LibraryProvider {

    protected static final MBNPlugin plugin = MBNPlugin.getInstance();
    protected static final ConfigManager configManager = plugin.getConfigManager();
}
