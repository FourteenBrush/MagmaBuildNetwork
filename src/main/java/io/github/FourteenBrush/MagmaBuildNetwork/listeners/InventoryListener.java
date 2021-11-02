package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.SafechestGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Instances;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiCreator)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClick() == ClickType.DROP) return;

        Player player = (Player) event.getWhoClicked();
        UUID inventoryUUID = GuiCreator.getOpenInventories().get(player.getUniqueId());
        if (inventoryUUID != null) {
            if (shouldCancel(event))
                event.setCancelled(true);
            GuiCreator gui = GuiCreator.getInventoriesByUuid().get(inventoryUUID);
            GuiCreator.GuiAction action = gui.getActions().get(event.getRawSlot());
            if (action != null) {
                action.click(player);
            }
        }
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getHolder() instanceof TradeGui) {
            Instances.COMMAND_TRADE.cancelTrade(player.getName());
        } else if (event.getInventory().getHolder() instanceof SafechestGui) {
            SafechestGui.getMenus().put(player.getUniqueId(), event.getInventory().getContents());
        }
        GuiCreator.getOpenInventories().remove(player.getUniqueId());
    }

    private boolean shouldCancel(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof TradeGui && (Instances.COMMAND_TRADE.getPlaceableSlots().contains(event.getRawSlot())) || event.getRawSlot() > 54) {
            return false;
        } else if (holder instanceof SafechestGui) {
            return false;
        }
        return true;
    }
}
