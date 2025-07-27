package org.whitepainter.wpnametag.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

public class GUIListener implements Listener {

    @EventHandler
    public void event(InventoryClickEvent event){
        Inventory clickedInventory = event.getClickedInventory();
        HumanEntity humanEntity = event.getWhoClicked();
        InventoryView inventoryView = humanEntity.getOpenInventory();
        Inventory topInventory = inventoryView.getTopInventory();
        int slot = event.getSlot();
        if(clickedInventory == null || clickedInventory != topInventory || slot < 0 || event.isCancelled()) return;
        InventoryHolder inventoryHolder = topInventory.getHolder();
        if(inventoryHolder instanceof NameTagGUI nameTagGUI){
            event.setCancelled(true);
            if(humanEntity instanceof Player player){
                if(player != nameTagGUI.getOwnerPlayer()){
                    player.sendMessage(ChatColor.RED+"这个界面不属于你，无法进行操作");
                    return;
                }
                nameTagGUI.clickTop(player, slot, event.getClick(), event.getHotbarButton());
            }
        }
    }
}
