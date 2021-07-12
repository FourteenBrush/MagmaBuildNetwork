package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Renderer;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class CommandCreatemap extends BaseCommand{


    @Override
    protected boolean execute(@NotNull String[] args) {

        MapView view = Bukkit.createMap(p.getWorld());
        view.getRenderers().clear();

        Renderer renderer = new Renderer();
        if (args.length < 1) {
            Utils.message(p, "§cPlease specify a link with an image!");
            return true;
        }
        if (!renderer.load(args[0])) {
            Utils.message(p, "§cImage could not be loaded!\n" +
                    "§cThe best images are from imgur");
            return true;
        }
        view.addRenderer(renderer);
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) map.getItemMeta();

        meta.setMapView(view);
        map.setItemMeta(meta);

        p.getInventory().addItem(map);
        Utils.message(p,"§aImage created!");

        ImageManager manager = ImageManager.getInstance();
        manager.saveImage(view.getId(), args[0]); // args[0] is the url
        return true;
    }
}
