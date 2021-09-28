package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.Openable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class LockListener implements Listener {

    private static final Main plugin = Main.getPlugin(Main.class);
    private static String blockOwner;

    /*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (!canLock(block)) return;

        Player p = event.getPlayer();
        NamespacedKey keyOwner = new NamespacedKey(plugin, block.getX() + "_" + block.getY() + "_" + block.getZ());
        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();
        // todo add support for adding multiple people to a lock

        blockOwner = container.get(keyOwner, PersistentDataType.STRING);

        if (blockOwner != null /*&& container.has(keyOwner, PersistentDataType.STRING) &&
                CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 1)) {
            // trying to remove lock
            if (cannotOpen(blockOwner, p)) {
                Utils.message(p, "&cYou cannot do this!");
            } else {
                container.remove(keyOwner);
                Utils.message(p, "&aLock removed!");
            }
            event.setCancelled(true);
        } else if (container.has(keyOwner, PersistentDataType.STRING) &&
                CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 0) && cannotOpen(blockOwner, p)) {
            // trying to set on something that's already locked
            event.setCancelled(true);
            Utils.message(p, "&cThis block is already locked!");
        } else if (CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 0)) {
            // normal lock
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString()); // apply the lock
            Utils.message(p, "&aLocked!");
        } else if (cannotOpen(blockOwner, p)) {
            // trying to open something with lock bypass
            if (CommandLock.getBypassingLock().contains(p.getUniqueId())) {
                Utils.message(p, "&aThis block is locked by " + Bukkit.getOfflinePlayer(UUID.fromString(blockOwner)).getName());
            } else {
                // trying to open something locked and not bypassing locks
                event.setCancelled(true);
                Utils.message(p, "&cYou cannot open this!");
            }
        }
    }*/

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (!canLock(block)) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        NamespacedKey keyOwner = new NamespacedKey(plugin, block.getX() + "_" + block.getY() + "_" + block.getZ());
        PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
        blockOwner = container.get(keyOwner, PersistentDataType.STRING);
        // removing lock
        if (CommandLock.getPlayersWantingLock().remove(uuid, 1)) {
            if (cannotOpen(blockOwner, player)) {
                Utils.message(player, "&cYou cannot remove the lock because you cannot open this!");
            } else {
                container.remove(keyOwner);
                Utils.message(player,"&aLock removed");
            }
            event.setCancelled(true);
        // placing lock
        } else if (CommandLock.getPlayersWantingLock().remove(uuid, 0)) {
            if (cannotOpen(blockOwner, player)) {
                Utils.message(player, "&cThis block is already locked!");
            } else {
                container.set(keyOwner, PersistentDataType.STRING, uuid.toString());
                Utils.message(player, "&aLocked!");
            }
            event.setCancelled(true);
        // normal interact
        } else if (cannotOpen(blockOwner, player)) {
            if (CommandLock.getPlayersBypassingLock().contains(uuid)) {
                Utils.message(player, "&aThis block is locked by " + Bukkit.getOfflinePlayer(UUID.fromString(blockOwner)).getName());
            } else {
                event.setCancelled(true);
                Utils.message(player, "&cYou cannot open this!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (cannotOpen(blockOwner, player) &&
                !CommandLock.getPlayersBypassingLock().contains(player.getUniqueId())) {
            event.setCancelled(true);
            Utils.message(player, "&cYou cannot break this");
        } else if (canLock(block)) {
            validateLock(block.getChunk());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        validateLock(event.getChunk());
    }

    public static boolean cannotOpen(String owner, Player opener) {
        if (owner == null || owner.isEmpty()) return false;
        return !owner.equalsIgnoreCase(opener.getUniqueId().toString());
    }

    public static String getOwner() {
        return blockOwner;
    }

    public static boolean canLock(Block b) {
        if (b == null) return false;
        BlockState state = b.getState();
        return (state instanceof TileState || state instanceof Lockable || b.getBlockData() instanceof Openable);
    }

    public static void validateLock(Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()) {
            if (!key.getNamespace().equalsIgnoreCase(plugin.getName())) return;
            String[] data = key.getKey().split("_");
            if (data.length != 3) continue;
            if (!canLock(chunk.getBlock(Integer.parseInt(data[0]) & 0b1111,
                    Integer.parseInt(data[1]), Integer.parseInt(data[2]) & 0b1111))) {
                container.remove(key);
                Utils.logInfo(String.format("Invalid lock removed! %s", (Object) key.getKey().split("_")));
            }
        }
    }
}