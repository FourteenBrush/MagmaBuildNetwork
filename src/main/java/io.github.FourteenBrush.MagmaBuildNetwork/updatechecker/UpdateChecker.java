package io.github.FourteenBrush.MagmaBuildNetwork.updatechecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateChecker implements Listener {

    private final Main plugin = Main.getInstance();
    private final int RESOURCE_ID = 1000;
    private final String pluginVersion = plugin.getDescription().getVersion();
    private String spigotVersion;
    private boolean updateAvailable;

    public boolean hasUpdateAvailable() {
        return this.updateAvailable;
    }

    public String getSpigotVersion() {
        return this.spigotVersion;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpsURLConnection con = (HttpsURLConnection) (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID)).openConnection();
                con.setRequestMethod("GET");
                this.spigotVersion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
            } catch (Exception ex) {
                plugin.getLogger().info("Failed to check for updates on spigot.");
                return;
            }
            if (this.spigotVersion == null || this.spigotVersion.isEmpty())
                return;
            this.updateAvailable = spigotIsNewer();
            if (!this.updateAvailable)
                return;
        });
    }

    private boolean spigotIsNewer() {
        if (this.spigotVersion == null || this.spigotVersion.isEmpty())
            return false;
        String plV = toReadable(this.pluginVersion);
        String spV = toReadable(this.spigotVersion);
        return (plV.compareTo(spV) < 0);
    }

    private String toReadable(String version) {
        if (version.contains("-DEV-"))
            version = version.split("-DEV-")[0];
        return version.replaceAll("\\.", "");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (Utils.hasPermission(event.getPlayer(), "notifyupdate"))
            plugin.getLogger().info("A new version of " + plugin.getDescription().getName() + "is available");
    }
}
