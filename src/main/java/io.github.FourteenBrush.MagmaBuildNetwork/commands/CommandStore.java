package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CommandStore extends BaseCommand {


    @Override
    protected boolean execute(@NotNull String[] args) {

        if (isConsole) return true;

        if(args.length > 0) {
            String message = Utils.getFinalArg(args, 0);
            ItemStack itemStack = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING)) {
                Utils.message(p, "§cThere is already a message stored inside this item!");
                Utils.message(p, "§aMessage: " + "§2" + container.get(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING));
            } else {
                container.set(new NamespacedKey(plugin, "MBN"), PersistentDataType.STRING, message);
                itemStack.setItemMeta(itemMeta);
                Utils.message(p, "§aMessage stored!");
            }
        } else {
            Utils.message(p,"§cYou need to provide a message to store!");
        }
        return true;
    }
}
