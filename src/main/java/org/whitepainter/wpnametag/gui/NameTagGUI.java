package org.whitepainter.wpnametag.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.whitepainter.wpnametag.NameTagManager;
import org.whitepainter.wpnametag.io.data.NameTag;
import org.whitepainter.wpnametag.io.data.PlayerData;
import org.whitepainter.wpnametag.io.instance.IOManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameTagGUI implements InventoryHolder {
    private final String owner;
    private final Inventory inventory;
    private final Map<Integer, Button> buttonMap = new HashMap<>();
    private int page = 1;

    public NameTagGUI(Player player){
        this.owner = player.getName();
        this.inventory = Bukkit.createInventory(this, 54, ChatColor.DARK_GREEN+""+ChatColor.BOLD+"称号背包");
        ItemStack is1 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta im1 = is1.getItemMeta();
        assert im1 != null;
        im1.setDisplayName("");
        is1.setItemMeta(im1);
        for(int i=0;i<9;i++){
            inventory.setItem(i, is1);
            inventory.setItem(i+45, is1);
        }
        {
            int[] pageSlots = {45, 53};
            String[] pageNames = {"<", ">"};
            for(int i=0;i<pageSlots.length;i++){
                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(ChatColor.WHITE.toString()+ChatColor.BOLD+pageNames[i]);
                itemStack.setItemMeta(itemMeta);
                inventory.setItem(pageSlots[i], itemStack);
            }
        }
        renew();
    }

    public String getOwner() {
        return owner;
    }

    public Player getOwnerPlayer(){
        String owner = getOwner();
        if(owner != null) return Bukkit.getPlayerExact(owner);
        return null;
    }

    @Override
    public @NotNull Inventory getInventory() {
        assert inventory != null;
        return inventory;
    }

    public Button getButtonBySlot(int slot){
        return buttonMap.get(slot);
    }

    public void clearButton(){
        buttonMap.clear();
    }

    public void bindButtonToSlot(int slot, Button button){
        buttonMap.put(slot, button);
    }

    public void bindButtonToSlot(ItemStack itemStack, int slot, Button button){
        buttonMap.put(slot, button);
        Inventory inventory = getInventory();
        inventory.setItem(slot, itemStack);
    }

    public void open(Player player){
        player.closeInventory();
        player.openInventory(inventory);
    }

    public int getAllPageNeedSlotAmount() {
        Player player = getOwnerPlayer();
        if(player == null) return 0;
        PlayerData playerData = IOManager.getPlayerData(getOwner());
        if(playerData == null) return 0;
        return playerData.getOwnedNameTag().size();
    }

    public int getPageSlotAmount() {
        return 36;
    }

    public int getMaxPage(){
        int size = getAllPageNeedSlotAmount();
        int pageSlot = getPageSlotAmount();
        return Math.max(size / pageSlot + (size % pageSlot > 0 ? 1 : 0), 1);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        int maxPage = getMaxPage();
        if(page < 1) page = 1;
        if(page > maxPage) page = maxPage;
        this.page = page;
        renew();
    }

    public void renew() {
        Player player = getOwnerPlayer();
        if(player == null) return;
        PlayerData playerData = IOManager.getPlayerData(getOwner());
        if(playerData == null) return;
        //物品清理
        Inventory inventory = getInventory();
        for(int i=9;i<45;i++){
            inventory.clear(i);
        }
        clearButton();
        //物品整理
        int page = getPage();
        int pageSlotAmount = getPageSlotAmount();
        int j = (page-1)*pageSlotAmount;
        List<NameTag> list = new ArrayList<>(playerData.getCanUsedNameTags());
        for(int slot=0;slot<=pageSlotAmount;slot++){
            int i = slot + j;
            if(i >= list.size()) break;
            NameTag nameTag = list.get(i);
            bindButtonToSlot(nameTag.getItemStack(), slot+9, new Button() {
                @Override
                public void onClick(Inventory inventory, Player clickPlayer, int slot, ClickType clickType, int hotbar) {
                    String key = nameTag.getKey();
                    if(NameTagManager.containsNameTag(key)){
                        if(playerData.setUsedNameTag(key)){
                            player.sendMessage("成功设置称号为 "+nameTag.getFormat());
                        }else{
                            player.sendMessage(ChatColor.RED+"设置称号失败，无法使用此称号");
                        }
                    }else{
                        player.sendMessage(ChatColor.RED+"无效的称号，请在稍后重试");
                    }
                    player.closeInventory();
                }
            });
        }
    }

    public boolean clickTop(Player player, int slot, ClickType clickType, int hotbar) {
        player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
        if(slot == 45){
            setPage(getPage()-1);
        }else if(slot == 53){
            setPage(getPage()+1);
        }else{
            Button button = getButtonBySlot(slot);
            if(button != null) button.onClick(getInventory(), player, slot, clickType, hotbar);
        }
        return false;
    }
}