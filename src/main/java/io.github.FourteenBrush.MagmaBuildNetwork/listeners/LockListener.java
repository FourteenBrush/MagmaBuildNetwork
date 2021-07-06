package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.Openable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class LockListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (!canLock(block)) return;

        Player p = event.getPlayer();
        String s = block.getX() + "_" + block.getY() + "_" + block.getZ();
        NamespacedKey keyOwner = new NamespacedKey(Main.getInstance(), s);
        Location location = block.getLocation();
        PersistentDataContainer container = location.getChunk().getPersistentDataContainer();

        final String[] data = keyOwner.getKey().split("_");
        String owner = container.get(keyOwner, PersistentDataType.STRING);

        if (owner != null && container.has(keyOwner, PersistentDataType.STRING) &&
                PlayerCommand.getPlayersWantingLock().contains(p.getUniqueId())) {
            event.setCancelled(true);
            Utils.message(p, "§cThis block is already locked!");
            PlayerCommand.getPlayersWantingLock().remove(p.getUniqueId());
        }

        if (PlayerCommand.getPlayersWantingLock().remove(p.getUniqueId())) {
            event.setCancelled(true);
            container.set(keyOwner, PersistentDataType.STRING, p.getUniqueId().toString()); // apply the lock
            Utils.message(p, "§2Locked!");
        } else {
            if (owner != null && !owner.equalsIgnoreCase(p.getUniqueId().toString())) {
                event.setCancelled(true);
                Utils.message(p, "§cYou cannot open th1s!");
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){

        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        Block block;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(Main.getInstance().getName())) {

                String[] data = key.getKey().split("_");
                if (data.length != 3) continue;

                block = event.getChunk().getBlock(Integer.parseInt(data[0]) & 0b1111, Integer.parseInt(data[1]), Integer.parseInt(data[2]) & 0b1111);

                if (!canLock(block)) {
                    Utils.logWarning(String.format("Unknown lock removed! %s", key.getKey()));
                    container.remove(key);
                }
            }
        }
    }

    private boolean canLock(Block b) {
        BlockState state = b.getState();
        return (state instanceof TileState || state instanceof Lockable || b.getBlockData() instanceof Openable);
    }
}