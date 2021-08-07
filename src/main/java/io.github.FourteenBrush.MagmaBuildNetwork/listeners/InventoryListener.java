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

        UUID inventoryUUID = GuiCreator.openInventories.get(player.getUniqueId());
        if (inventoryUUID != null) {
            if (!(event.getInventory() instanceof TradeGui && (CommandTrade.getPlaceableSlots()
                    .contains(event.getSlot()) || event.getRawSlot() > 54)))
            event.setCancelled(true);
            GuiCreator gui = GuiCreator.getInventoriesByUUID().get(inventoryUUID);
            GuiCreator.GuiAction action = gui.getActions().get(event.getSlot());

            if (action != null) {
                action.click(player);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if (event.getInventory() instanceof TradeGui) {
            CommandTrade.cancel();
        } else if (event.getInventory() instanceof CommandSafechest) {
            CommandSafechest.getMenus().put(player.getUniqueId(), event.getInventory().getContents());
        }
        GuiCreator.openInventories.remove(player.getUniqueId());
    }
}
