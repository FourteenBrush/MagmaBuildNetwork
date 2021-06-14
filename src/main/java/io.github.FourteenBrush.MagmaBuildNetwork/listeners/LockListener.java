package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Openable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class LockListener implements Listener {

    private final HashMap<UUID, Location> locks = new HashMap<>();
    private final NamespacedKey keyOwner = new NamespacedKey(Main.getInstance(), "Owner");
    private final NamespacedKey keyMember = new NamespacedKey(Main.getInstance(), "Member");

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        BlockState blockState = block.getState();

        if (!(blockState instanceof TileState) || (!(blockState instanceof Lockable) || (!(blockState instanceof Openable)))) return;

        Location location = block.getLocation();
        PersistentDataContainer container = location.getChunk().getPersistentDataContainer();

        if (container.has(keyOwner, PersistentDataType.STRING) && Main.getPlayersWantingLock()
                .contains(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "This block is already locked!");
            Main.getPlayersWantingLock().remove(p.getUniqueId());
        }
        if (Main.getPlayersWantingLock().remove(p.getUniqueId())) {
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString());
            blockState.update(); // apply the lock!
            p.sendMessage(ChatColor.DARK_GREEN + "Locked!");
        } else {
            String owner = container.get(keyOwner, PersistentDataType.STRING);
            if (owner != null && !owner.equalsIgnoreCase(p.getUniqueId().toString())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot open this!");
            }
        }

        if (!(locks.containsKey(p.getUniqueId()) && locks.containsValue(location))) {
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You cannot open this!");
        }
    }
}
