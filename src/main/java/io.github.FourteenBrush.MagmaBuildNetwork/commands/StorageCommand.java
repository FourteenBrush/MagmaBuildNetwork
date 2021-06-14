package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class StorageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(args.length > 0) {

                StringBuilder message = new StringBuilder();
                for(String arg : args) {
                    message.append(arg + " ");
                }

                ItemStack itemStack = p.getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemStack.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if(container.has(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING)) {
                    p.sendMessage(ChatColor.GREEN + "There is already a message stored inside this item!");
                    p.sendMessage(ChatColor.GREEN + "Message: " + ChatColor.GREEN + container.get(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING));
                }
                else {
                    container.set(new NamespacedKey(Main.getInstance(), "MBN"), PersistentDataType.STRING, message.toString());

                    itemStack.setItemMeta(itemMeta);

                    p.sendMessage(ChatColor.GREEN + "Message stored!");
                }
            }
            else {
                p.sendMessage(ChatColor.RED + "You need to provide a message to store!");
            }
        }
        return true;
    }
}
