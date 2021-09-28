package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CommandStore extends AbstractCommand {

    private static final NamespacedKey key = new NamespacedKey(plugin, "storedMessageMBN");

    public CommandStore() {
        super("store", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length > 0) {
            ItemStack itemStack = executor.getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                Utils.message(executor, "&cThere is already a message stored inside this item!",
                        "&aMessage: " + "&2" + container.get(key, PersistentDataType.STRING));
                return true;
            }
            container.set(key, PersistentDataType.STRING, String.join(" ", args));
            itemStack.setItemMeta(itemMeta);
            Utils.message(executor, "&aMessage stored!");
            return true;
        }
        Utils.message(executor,"&cYou need to provide a message to store!");
        return true;
    }
}
