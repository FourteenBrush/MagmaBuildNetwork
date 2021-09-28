package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSafechest;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClick().equals(ClickType.DROP)) return;
        Player player = (Player) event.getWhoClicked();
        UUID inventoryUUID = GuiCreator.getOpenInventories().get(player.getUniqueId());
        if (inventoryUUID == null) return;
        if (shouldCancel(event))
            event.setCancelled(true);
        GuiCreator.GuiAction action = GuiCreator.getInventoriesByUUID().get(inventoryUUID).getActions()
                .get(event.getRawSlot());
        if (action != null) {
            action.click(player);
        }
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory() instanceof TradeGui) {
            CommandTrade.cancel();
        } else if (event.getView().getTitle().endsWith("safechest")) { // todo instanceof doesnt work
            CommandSafechest.getMenus().put(player.getUniqueId(), event.getInventory().getContents());
        }
        GuiCreator.getOpenInventories().remove(player.getUniqueId());
    }

    private boolean shouldCancel(InventoryClickEvent event) {
        return !(event.getInventory() instanceof TradeGui && (CommandTrade.getPlaceableSlots().contains(event.getSlot()) || event.getRawSlot() > 54));
    }
}
