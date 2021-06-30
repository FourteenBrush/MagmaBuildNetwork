package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Effects;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.util.ParticleData;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Utils;
import io.github.FourteenBrush.MagmaBuildNetwork.events.RightClickNPCEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage(Utils.colorize("&7[&a&l+&7] &b" + p.getName() + " &7joined the server."));
        PacketReader reader = new PacketReader();
        reader.inject(p);
        if (NPC.getNPCs() == null || NPC.getNPCs().isEmpty()) {
            return;
        }
        NPC.addJoinPacket(p);
        if (!p.hasPlayedBefore()) {
            p.getInventory().addItem(new ItemStack(Material.APPLE, 16));
            p.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
        }
        TextComponent message = new TextComponent("Discord");
        message.setColor(ChatColor.DARK_PURPLE);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/KWNYMDGX7H"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click HERE to get a link to the Discord server.")));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        PacketReader reader = new PacketReader();
        reader.unInject(p);
        event.setQuitMessage(Utils.colorize("&7[&c&l-&7] &b" + p.getName() + " &7left the server."));
        ParticleData d = new ParticleData(p.getUniqueId());
        if (d.hasID())
            d.endTask();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (PlayerCommand.getFrozenPlayers().contains(p.getUniqueId())) {
            if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {
                event.setCancelled(true);
            }
        }
        if (!ParticleData.hasWalkTrail(p.getUniqueId()))
            return;
        Random r = new Random();
        for (int i = 0; i < 5; i++)
            p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                    r.nextDouble() * 0.5, r.nextDouble() * 0.5, r.nextDouble() * .5),0);
        for (int i = 0; i < 5; i++)
            p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(
                    -1 * (r.nextDouble() * 0.5), r.nextDouble() * 0.5, (r.nextDouble() * .5) * -1),0);
    }

    @EventHandler
    public void onClick(RightClickNPCEvent event) {
        Player p = event.getPlayer();
        if (event.getNPC().getId() == 1) {
            p.sendMessage(ChatColor.DARK_PURPLE + "Hello! I'm " + event.getNPC().getName() + " :)");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        GUI trailsMenu = new GUI();
        GUI tradeMenu = new GUI();

        if (!(event.getInventory().getHolder() instanceof GUI)) {
            return;
        }
        if (event.getInventory().equals(trailsMenu)/*trails*/) {
            event.setCancelled(true);

            if (event.getView().getType() == InventoryType.PLAYER) {
                return;
            }
            ParticleData particle = new ParticleData(p.getUniqueId());

            // trail activated
            if (particle.hasID()) {
                particle.endTask();
                particle.removeID();
            }

            Effects effect = new Effects(p);

            switch(event.getSlot()) {
                case 3:
                    effect.startTotem();
                    p.closeInventory();
                    p.updateInventory();
                    break;
                case 5:
                    particle.setID(1);
                    p.closeInventory();
                    p.updateInventory();
                    break;
                case 8:
                    p.closeInventory();
                    break;
                default:
                    break;
            }

        } else if (event.getInventory().equals(tradeMenu)/*trade menu*/) {
            tradeMenu.registerTrade();
        }

    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            if (player == null) // if mobs died of a natural dead return
                return;
            Random r = new Random();
            int amount = r.nextInt(10) + 10;
            Main.getInstance().eco.depositPlayer(player, amount);
            player.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "+ $" + amount);
        }
    }

    private boolean canLock(BlockState state) {

        return (state instanceof TileState || state instanceof Lockable || state.getBlockData() instanceof Openable);
    }

    /*@EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        BlockState blockState = block.getState();

        if (!(blockState instanceof TileState) || (!(blockState instanceof Lockable) || (!(blockState instanceof Openable)))) return;

        String s = block.getX() + "|" + block.getY() + "|" + block.getZ();
        NamespacedKey chunkKey = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        TileState tileState = (TileState) blockState;
        PersistentDataContainer ChunkContainer = tileState.getPersistentDataContainer();

        if (container.has(keyOwner, PersistentDataType.STRING) && MagmaBuildNetwork.getPlayersWantingLock()
            .contains(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "This block is already locked!");
            MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId());
        }
        if (MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId())) {
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
    }*/

    /*@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Player p = event.getPlayer();

        BlockState blockState = block.getState();
        if (!(blockState instanceof TileState) || (!(blockState instanceof Lockable))) return;

        TileState tileState = (TileState) blockState;

        PersistentDataContainer container = tileState.getPersistentDataContainer();

        String lock = container.get(keyOwner, PersistentDataType.STRING);
        if (lock != null && !lock.equalsIgnoreCase(p.getUniqueId().toString())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break this!");
        }
    }*/

    /*@EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        BlockState blockState = block.getState();

        if (!canLock(blockState)) return;

        String s = block.getX() + "/" + block.getY() + "/" + block.getZ();
        NamespacedKey key = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            System.out.println("A Lock exists for this block!");
            if (container.get(key, PersistentDataType.STRING).equals(event.getPlayer().getUniqueId().toString())) {
                System.out.println("The lock is yours!");
            } else {
                System.out.println("The lock is NOT yours!");
                event.setCancelled(true);
            }
            return;
        } else {
            System.out.println("No Lock exists for this block!");
        }

        container.set(key, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
    }*/

     // Clean up any left over lock data that is no longer valid.
    /*@EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {

        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        BlockState blockState;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(MagmaBuildNetwork.getPlugin().getName())) {

                String[] data = key.getKey().split("/");
                if (data.length != 3) continue;

                blockState = event.getChunk().getBlock(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2])).getState();

                if (!canLock(blockState)) {
                    MagmaBuildNetwork.getPlugin().getLogger().warning(String.format("Unknown Lock removed! %s.", key.getKey()));
                    container.remove(key);
                }
            }
        }
    }*/
}
   /* @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        BlockState blockState = block.getState();

        if (!canLock(blockState)) return;

        String s = block.getX() + "/" + block.getY() + "/" + block.getZ();
        NamespacedKey key = new NamespacedKey(MagmaBuildNetwork.getPlugin(), s);

        PersistentDataContainer container = block.getLocation().getChunk().getPersistentDataContainer();

        TileState tileState = (TileState) blockState;
        PersistentDataContainer ChunkContainer = tileState.getPersistentDataContainer();

        if (container.has(keyOwner, PersistentDataType.STRING) && MagmaBuildNetwork.getPlayersWantingLock()
                .contains(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "This block is already locked!");
            MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId());
        }
        if (MagmaBuildNetwork.getPlayersWantingLock().remove(p.getUniqueId())) {
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
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){

        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        BlockState blockState;

        for (NamespacedKey key : container.getKeys()) {
            if (key.getNamespace().equalsIgnoreCase(MagmaBuildNetwork.getPlugin().getName())) {

                String[] data = key.getKey().split("|");
                if (data.length != 3) continue;

                blockState = event.getChunk().getBlock(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2])).getState();

                if (!(blockState instanceof TileState || blockState instanceof Lockable || blockState instanceof Openable)) {
                    MagmaBuildNetwork.getPlugin().getLogger().warning(String.format("Unknown Lock removed! %s.", key.getKey()));
                    container.remove(key);
                }

            }
        }
    }*/