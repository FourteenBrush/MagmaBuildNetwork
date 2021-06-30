package io.github.FourteenBrush.MagmaBuildNetwork.util;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Renderer extends MapRenderer {

    private BufferedImage image;
    private boolean done;

    public Renderer() {
        done = false;
    }

    public Renderer(String url) {
        load(url);
        done = false;
    }

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (done)
            return;
        canvas.drawImage(0, 0, image);
        view.setTrackingPosition(false);
        done = true;
    }

    public boolean load(String url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
            image = MapPalette.resizeImage(image);
        } catch (IOException e) {
            return false;
        }
        this.image = image;
        return true;
    }
}
