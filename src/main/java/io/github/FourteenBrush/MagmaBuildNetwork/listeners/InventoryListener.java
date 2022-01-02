package io.github.FourteenBrush.MagmaBuildNetwork.listeners;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.GuiCreator;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.SafechestGui;
import io.github.FourteenBrush.MagmaBuildNetwork.gui.TradeGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;
import java.util.function.Consumer;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiCreator)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClick() == ClickType.DROP) return;
        Player player = (Player) event.getWhoClicked();
        UUID inventoryUUID = GuiCreator.getOpenInventories().get(player.getUniqueId());
        if (inventoryUUID == null) return;
        if (shouldCancel(event))
            event.setCancelled(true);
        Consumer<InventoryClickEvent> action = GuiCreator.getInventoriesByUuid().get(inventoryUUID)
                .getActions().get(event.getRawSlot());
        if (action != null) {
            action.accept(event);
        }
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getHolder() instanceof TradeGui) {
            CommandTrade.getInstance().cancelTrade(player.getName());
        } else if (event.getInventory().getHolder() instanceof SafechestGui) {
            SafechestGui.getMenus().put(player.getUniqueId(), event.getInventory().getContents());
        }
        GuiCreator.getOpenInventories().remove(player.getUniqueId());
    }

    private boolean shouldCancel(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof TradeGui && (CommandTrade.getInstance().getPlaceableSlots().contains(event.getRawSlot())) || event.getRawSlot() > 54) {
            return false;
        } else return !(holder instanceof SafechestGui);
    }
}
