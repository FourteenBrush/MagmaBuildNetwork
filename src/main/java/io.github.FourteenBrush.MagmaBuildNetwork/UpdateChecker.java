package io.github.FourteenBrush.MagmaBuildNetwork;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

   private int resourceID;

    public UpdateChecker(int resourceID) {
        this.resourceID = resourceID;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID).openStream();
                Scanner scanner = new Scanner(inputStream);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }

    } catch (IOException e ) {
                Main.getInstance().getLogger().info("Something went wrong when trying to find a new version " + e.getMessage());
            }
        });
    }
}