package org.whitepainter.wpnametag.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public abstract class Button {
    
    public abstract void onClick(Inventory inventory, Player clickPlayer, int slot, ClickType clickType, int hotbar);

}
