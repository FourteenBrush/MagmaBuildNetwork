package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Chunk;
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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.Openable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class LockListener implements Listener {

    private static final Main plugin = Main.getInstance();
    private static String blockOwner;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (!canLock(block)) return;

        Player p = event.getPlayer();
        NamespacedKey keyOwner = new NamespacedKey(plugin, block.getX() + "_" + block.getY() + "_" + block.getZ());
        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();
        // todo add support for adding multiple people to a lock

        final String[] data = keyOwner.getKey().split("_");
        final String owner = container.get(keyOwner, PersistentDataType.STRING);
        blockOwner = owner;

        if (owner != null && container.has(keyOwner, PersistentDataType.STRING) &&
                CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 1)) {
            // trying to remove lock
            if (cannotOpen(owner, p)) {
                event.setCancelled(true);
                Utils.message(p, "§cYou cannot do this!");
                return;
            }
            event.setCancelled(true);
            Utils.message(p, "§aLock removed!");
            container.remove(keyOwner);
        } else if (container.has(keyOwner, PersistentDataType.STRING) &&
                CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 0) && cannotOpen(owner, p)) {
            // trying to set on something that's already locked
            event.setCancelled(true);
            Utils.message(p, "§cThis block is already locked!");
            CommandLock.getPlayersWantingLock().remove(p.getUniqueId());
        } else if (CommandLock.getPlayersWantingLock().remove(p.getUniqueId(), 0)) {
            // normal lock
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString()); // apply the lock
            Utils.message(p, "§aLocked!");
        } else if (cannotOpen(owner, p)) {
            // trying to open something with lock bypass
            if (CommandLock.getBypassingLock().contains(p.getUniqueId())) {
                Utils.message(p, "§aThis block is locked by " + owner);
            } else {
                // trying to open something locked and not bypassing locks
                event.setCancelled(true);
                Utils.message(p, "§cYou cannot open this!");
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
       /* PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        Block block;
        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(plugin.getName())) {
                String[] locationData = key.getKey().split("_");
                if (locationData.length != 3) continue;
                block = event.getChunk().getBlock(Integer.parseInt(locationData[0]) & 0b1111,
                        Integer.parseInt(locationData[1]), Integer.parseInt(locationData[2]) & 0b1111);
                if (!canLock(block)) {
                    Utils.logInfo(String.format("Invalid lock removed! %s", (Object) key.getKey().split("_")));
                    container.remove(key);
                }
            }
        }*/
        checkLockStillValid(event.getChunk());
    }

    public static boolean cannotOpen(String owner, Player opener) {
        // owner is different than the opener
        return owner != null && !owner.equalsIgnoreCase(opener.getUniqueId().toString());
    }

    public static String getOwner() {
        return blockOwner;
    }

    public static boolean canLock(Block b) {
        BlockState state = b.getState();
        return (state instanceof TileState || state instanceof Lockable || b.getBlockData() instanceof Openable);
    }

    public static void checkLockStillValid(Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        Block block;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(plugin.getName())) {

                String[] data = key.getKey().split("_");
                if (data.length != 3) continue;

                block = chunk.getBlock(Integer.parseInt(data[0]) & 0b1111,
                        Integer.parseInt(data[1]), Integer.parseInt(data[2]) & 0b1111);
                if (!canLock(block)) {
                    Utils.logInfo(String.format("Invalid lock removed! %s", (Object) key.getKey().split("_")));
                    container.remove(key);
                }
            }
        }
    }
}