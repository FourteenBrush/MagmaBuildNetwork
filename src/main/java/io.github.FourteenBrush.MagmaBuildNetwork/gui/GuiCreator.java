package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class GuiCreator extends ItemBuilder {

    private static final Map<UUID, GuiCreator> inventoriesByUUID = new HashMap<>();
    private static final Map<UUID, UUID> openInventories = new HashMap<>(); // player id and gui id

    private final Map<Integer, GuiAction> actions;
    private final UUID uuid;

    protected Inventory inv;

    public GuiCreator(String invName, int rows) {
        uuid = UUID.randomUUID();
        inv = Bukkit.createInventory(null, rows * 9, invName);
        actions = new HashMap<>();
        inventoriesByUUID.put(uuid, this);
    }

    public void setItem(int slot, ItemStack stack, GuiAction action) {
        inv.setItem(slot, stack);
        if (action != null){
            actions.put(slot, action);
        }
    }

    public void setItem(int slot, ItemStack stack){
        setItem(slot, stack, null);
    }

    protected ItemStack createItem(Material material, String displayName, List<String> lore) {
        return new ItemBuilder(material).setLore(lore).setDisplayName(displayName).build();
    }

    protected ItemStack createItem(Material material, int amount, String displayName, List<String> lore) {
        ItemStack itemStack = createItem(material, displayName, lore);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public void open(Player player) {
        player.openInventory(inv);
        openInventories.put(player.getUniqueId(), uuid);
    }

    public void delete() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID u = openInventories.get(player.getUniqueId());
            if (u.equals(uuid)) player.closeInventory();
            inventoriesByUUID.remove(uuid);
        });
        inventoriesByUUID.remove(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Inventory getInv() {
        return inv;
    }

    public static Map<UUID, GuiCreator> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    public Map<Integer, GuiAction> getActions() {
        return actions;
    }

    public interface GuiAction {
        void click(Player player);
        default void ex() {}
    }
}
