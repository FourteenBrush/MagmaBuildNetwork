package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class LockListener implements Listener {

    private final MBNPlugin plugin;
    private UUID blockOwner;

    public LockListener(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (!canLock(block)) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        NamespacedKey keyOwner = new NamespacedKey(plugin, block.getX() + "_" + block.getY() + "_" + block.getZ());
        PersistentDataContainer container = block.getChunk().getPersistentDataContainer();
        String value = container.get(keyOwner, PersistentDataType.STRING);
        if (value == null) return;
        blockOwner = UUID.fromString(value);
        // removing lock
        if (CommandLock.getPlayersWantingLock().remove(uuid, 1)) {
            if (cannotOpen(blockOwner, uuid)) {
                PlayerUtils.message(player, "&cYou cannot remove the lock because you cannot open this!");
            } else {
                container.remove(keyOwner);
                PlayerUtils.message(player,"&6Lock removed");
            }
            event.setCancelled(true);
        // placing lock
        } else if (CommandLock.getPlayersWantingLock().remove(uuid, 0)) {
            if (cannotOpen(blockOwner, uuid)) {
                PlayerUtils.message(player, "&cThis block is already locked!");
            } else {
                // check if locking block is a double chest
                Location loc2 = getDoubleChestLocationIfIs(block.getState());
                if (loc2 != null) container.set(new NamespacedKey(plugin, loc2.getX() + "_" + loc2.getY() + "_" + loc2.getZ())
                    , PersistentDataType.STRING, uuid.toString());
                container.set(keyOwner, PersistentDataType.STRING, uuid.toString());
                PlayerUtils.message(player, "&6Locked!");
            }
            event.setCancelled(true);
        // normal interact
        } else if (cannotOpen(blockOwner, uuid)) {
            if (CommandLock.getPlayersBypassingLock().contains(uuid)) {
                PlayerUtils.message(player, "&6This block is locked by " + Bukkit.getOfflinePlayer(blockOwner).getName());
            } else {
                event.setCancelled(true);
                PlayerUtils.message(player, "&cYou cannot open this!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (cannotOpen(blockOwner, player.getUniqueId()) &&
                !CommandLock.getPlayersBypassingLock().contains(player.getUniqueId())) {
            event.setCancelled(true);
            PlayerUtils.message(player, "&cYou cannot break this");
        } else if (canLock(block)) {
            validateLock(block.getChunk());
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        validateLock(event.getChunk());
    }

    public boolean cannotOpen(UUID owner, UUID opener) {
        if (owner == null) return false;
        return !owner.equals(opener);
    }

    public boolean canLock(Block block) {
        if (block == null) return false;
        BlockState state = block.getState();
        return (state instanceof TileState || state instanceof Lockable || block.getBlockData() instanceof Openable);
    }

    public void validateLock(Chunk chunk) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()) {
            if (!key.getNamespace().equalsIgnoreCase("magmabuildnetwork")) return;
            String[] data = key.getKey().split("_");
            if (data.length != 3) continue;
            if (!canLock(chunk.getBlock(Integer.parseInt(data[0]) & 0b1111,
                    Integer.parseInt(data[1]) & 0b1111, Integer.parseInt(data[2]) & 0b1111))) {
                container.remove(key);
                Utils.logInfo("Invalid lock removed! " + Arrays.toString(data));
            }
        }
    }

    @Nullable
    private Location getDoubleChestLocationIfIs(BlockState state) {
        if (state instanceof Chest) {
            Chest chest = (Chest) state;
            Inventory inventory = chest.getInventory();
            if (inventory instanceof DoubleChestInventory) {
                return ((DoubleChest) inventory.getHolder()).getLocation();
            }
        }
        return null;
    }
}