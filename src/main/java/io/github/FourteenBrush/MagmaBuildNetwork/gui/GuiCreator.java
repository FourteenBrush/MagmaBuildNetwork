package io.github.FourteenBrush.MagmaBuildNetwork.gui;

import io.github.FourteenBrush.MagmaBuildNetwork.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class GuiCreator {

    public static final Map<UUID, GuiCreator> inventoriesByUUID = new HashMap<>();
    public static final Map<UUID, UUID> openInventories = new HashMap<>(); // player id and gui id

    private final UUID uuid;
    protected Inventory inv;
    private final Map<Integer, GuiAction> actions;

    public GuiCreator(String invName, int rows) {
        uuid = UUID.randomUUID();
        inv = Bukkit.createInventory(null, rows * 9, invName);
        actions = new HashMap<>();
        inventoriesByUUID.put(getUuid(), this);
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

    public void open(Player p) {
        p.openInventory(inv);
        openInventories.put(p.getUniqueId(), getUuid());
    }

    public void delete() {
        for (Player p : Bukkit.getOnlinePlayers()){
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUuid())){
                p.closeInventory();
            }
        }
        inventoriesByUUID.remove(getUuid());
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
    }

    protected ItemStack createItem(Material material, String displayName, List<String> lore) {
        ItemBuilder itemBuilder = new ItemBuilder(material);
        return itemBuilder.build(material, displayName, lore);
    }

    protected ItemStack createItem(Material material, int amount, String displayName, List<String> lore) {
        ItemStack itemStack = createItem(material, displayName, lore);
        itemStack.setAmount(amount);
        return itemStack;
    }
}
