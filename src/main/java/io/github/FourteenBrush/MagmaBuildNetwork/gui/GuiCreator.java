package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.MBNPlugin;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class GuiCreator extends ItemBuilder implements InventoryHolder {

    private static final Map<UUID, UUID> OPEN_INVENTORIES = new HashMap<>(); // player id and gui id
    private static final Map<UUID, GuiCreator> INVENTORIES_BY_UUID = new HashMap<>();

    private final Map<Integer, Consumer<InventoryClickEvent>> actions;
    private final UUID uuid;

    protected final MBNPlugin plugin;
    protected Inventory inv;
    protected Player player;

    public GuiCreator(String invName, int rows) {
        plugin = MBNPlugin.getInstance();
        uuid = UUID.randomUUID();
        inv = Bukkit.createInventory(this, rows * 9, Utils.colorize(invName));
        actions = new HashMap<>();
        INVENTORIES_BY_UUID.put(uuid, this);
    }

    protected void setItem(int slot, ItemStack stack){
        setItem(slot, stack, null);
    }

    protected void setItem(int slot, Material material, Consumer<InventoryClickEvent> onClick) {
        setItem(slot, new ItemStack(material), onClick);
    }

    protected void setItem(int slot, ItemStack stack, Consumer<InventoryClickEvent> onClick) {
        inv.setItem(slot, stack);
        if (onClick != null)
            actions.put(slot, onClick);
    }

    protected ItemStack createItem(Material material, String displayName) {
        return createItem(material, displayName, null);
    }

    protected ItemStack createItem(Material material, String displayName, List<String> lore) {
        return new ItemBuilder(material)
                .setDisplayName(displayName)
                .setLore(lore)
                .build();
    }

    protected ItemStack createItem(Material material, int amount, String displayName) {
        ItemStack itemStack = createItem(material, displayName, null);
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

    public Map<Integer, Consumer<InventoryClickEvent>> getActions() {
        return actions;
    }
}
