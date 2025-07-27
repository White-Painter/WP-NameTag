package org.whitepainter.wpnametag.io.data;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whitepainter.wpnametag.Setting;

public class NameTag {
    private final String key;
    private final ItemStack itemStack;

    public NameTag(String key, ItemStack itemStack) {
        this.key = key;
        this.itemStack = itemStack;
    }

    public String getKey() {
        return this.key;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getDisplayName() {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        assert itemMeta != null;
        return itemMeta.getDisplayName();
    }

    public String getPermission(){
        return "wpnametag."+getKey();
    }

    public String getFormat(){
        return Setting.nameTagPrefix+getDisplayName()+Setting.nameTagSuffix;
    }
}
