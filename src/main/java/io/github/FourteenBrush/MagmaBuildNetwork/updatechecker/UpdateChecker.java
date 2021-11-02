package io.github.FourteenBrush.MagmaBuildNetwork.updatechecker;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;

public class UpdateChecker {

    private final Main plugin;
    private final int resourceId;

    public UpdateChecker(int resourceId) {
        plugin = Main.getPlugin(Main.class);
        this.resourceId = resourceId;
    }

    public CompletableFuture<UpdateResult> checkUpdate() {
        return CompletableFuture.supplyAsync(() -> {
            String latestVersion = getLatestVersion();
            if (latestVersion == null) {
                return new UpdateResult(VersionResult.ERROR);
            }
            String strippedLatest = latestVersion.replaceAll("[^A-Za-z0-9]", "");
            String strippedCurrent = plugin.getDescription().getVersion().replaceAll("[^A-Za-z0-9]", "");
            return strippedCurrent.equals(strippedLatest) ? new UpdateResult(VersionResult.NONE) : new UpdateResult(VersionResult.AVAILABLE, latestVersion);
        });
    }

    public CompletableFuture<DownloadResult> downloadUpdate() {
        File current = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        File updateFile = new File(plugin.getServer().getUpdateFolderFile(), current.getName());
        return downloadFile(updateFile, "FILL THIS IN");
    }

    private CompletableFuture<DownloadResult> downloadFile(File filePath, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL web = new URL(url);
                try (InputStream in = web.openStream()) {
                    Files.createDirectories(filePath.getParentFile().toPath());
                    Files.copy(in, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                return DownloadResult.SUCCESSFUL;
            } catch (IOException e) {
                e.printStackTrace();
                return DownloadResult.ERROR;
            }
        });
    }

    private String getLatestVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum VersionResult {
        AVAILABLE,
        ERROR,
        NONE
    }

    private enum DownloadResult {
        ERROR,
        SUCCESSFUL
    }

    public static class UpdateResult {

        private final VersionResult versionResult;
        private String newVersion;

        public UpdateResult(VersionResult versionResult) {
            this.versionResult = versionResult;
        }

        public UpdateResult(VersionResult versionResult, String newVersion) {
            this.versionResult = versionResult;
            this.newVersion = newVersion;
        }

        public VersionResult getResult() {
            return versionResult;
        }

        public String getNewVersion() {
            return newVersion;
        }
    }
}
