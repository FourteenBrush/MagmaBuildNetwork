package io.github.FourteenBrush.MagmaBuildNetwork.dependencies;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion implements Listener {

    private static final Main plugin = Main.getInstance();

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
