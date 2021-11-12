package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PlayerUtils {

    private PlayerUtils() {}

    public static void message(CommandSender sender, String... messages) {
        for (String message : messages) {
            sender.sendMessage(Utils.colorize(message));
        }
    }

    public static void message(Player player, BaseComponent... baseComponents) {
        player.spigot().sendMessage(baseComponents);
    }

    public static void broadcast(String message, boolean broadcastConsole) {
        if (broadcastConsole) Bukkit.broadcastMessage(Utils.colorize(message));
        else Bukkit.getOnlinePlayers().forEach(player -> message(player, message));
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.colorize(message)));
    }

    public static boolean checkPlayerOnline(CommandSender messageTarget, @Nullable Player playerToCheck, boolean exactName) {
        if (playerToCheck == null || !Bukkit.getOnlinePlayers().contains(playerToCheck)) {
            message(messageTarget, Lang.PLAYER_NOT_ONLINE.get(""));
            return false;
        }
        return true;
    }

    public static void giveOrDropFor(Player player, ItemStack... itemStacks) {
        player.getInventory().addItem(itemStacks).values().forEach(overFlow -> player.getWorld().dropItem(player.getLocation(), overFlow));
    }

    public static void addAtOrDrop(Player player, int slot, ItemStack itemStack) {
        ItemStack at = player.getInventory().getItem(slot);
        if (at == null || at.getType() == Material.AIR) slot = player.getInventory().firstEmpty();
        if (slot > -1) player.getInventory().setItem(slot, itemStack);
        else player.getWorld().dropItem(player.getLocation(), itemStack);
    }
}
