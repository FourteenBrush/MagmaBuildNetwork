package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class GuiCreator extends ItemBuilder implements InventoryHolder {

    private static final Map<UUID, UUID> OPEN_INVENTORIES = new HashMap<>(); // player id and gui id
    private static final Map<UUID, GuiCreator> INVENTORIES_BY_UUID = new HashMap<>();

    private final Map<Integer, GuiAction> actions;
    private final UUID uuid;

    protected final Main plugin;
    protected Inventory inv;
    protected Player player;

    public GuiCreator(String invName, int rows) {
        plugin = Main.getPlugin(Main.class);
        uuid = UUID.randomUUID();
        inv = Bukkit.createInventory(this, rows * 9, Utils.colorize(invName));
        actions = new HashMap<>();
        INVENTORIES_BY_UUID.put(uuid, this);
    }

    public void setItem(int slot, Material material, GuiAction action) {
        setItem(slot, new ItemStack(material), action);
    }

    public void setItem(int slot, ItemStack stack){
        setItem(slot, stack, null);
    }

    public void setItem(int slot, ItemStack stack, GuiAction action) {
        inv.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    protected ItemStack createItem(Material material, String displayName, List<String> lore) {
        return new ItemBuilder(material)
                .setDisplayName(displayName)
                .setLore(lore)
                .build();
    }

    protected ItemStack createItem(Material material, int amount, String displayName, List<String> lore) {
        ItemStack itemStack = createItem(material, displayName, lore);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public void open(Player player) {
        this.player = player;
        player.openInventory(inv);
        OPEN_INVENTORIES.put(player.getUniqueId(), uuid);
    }

    public void delete() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID u = OPEN_INVENTORIES.get(player.getUniqueId());
            if (u.equals(uuid)) player.closeInventory();
        });
        INVENTORIES_BY_UUID.remove(uuid);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inv;
    }

    public static Map<UUID, GuiCreator> getInventoriesByUuid() {
        return INVENTORIES_BY_UUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return OPEN_INVENTORIES;
    }

    public Map<Integer, GuiAction> getActions() {
        return actions;
    }

    public interface GuiAction {
        void click(Player player);
    }
}
