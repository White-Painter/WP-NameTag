package org.whitepainter.wpnametag;

import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whitepainter.wpnametag.io.data.NameTag;
import org.whitepainter.wpnametag.io.instance.IOManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class NameTagManager {
    private static final String nameTagTable = Setting.nameTagTableNameTag;
    private static final String playerTable = Setting.nameTagTablePlayer;
    private static final Map<String, NameTag> nameTagMap = new HashMap<>();
    private static NameTag defaultNameTag;

    protected static void init() {
        IOManager ioManager = IOManager.getInstance();
        if(!ioManager.existTable(nameTagTable)) ioManager.createBasicTable(nameTagTable, "`itemstack` TEXT");
        if(!ioManager.existTable(playerTable)) ioManager.createBasicTable(playerTable, "`used` VARCHAR(16), `own` TEXT");
        NameTagManager.reload();
    }

    public static Collection<NameTag> getNameTagFromSQL() {
        List<NameTag> list = new ArrayList<>();
        IOManager ioManager = IOManager.getInstance();
        ResultSet rs = ioManager.query("Select * From "+nameTagTable);
        try {
            while (rs.next()){
                String key = rs.getString("key");
                String data = rs.getString("itemstack");
                list.add(new NameTag(key, getItemStackFromData(data)));
            }
        } catch (SQLException exception) {
            exception.fillInStackTrace();
        }
        return list;
    }

    public static void reload() {
        nameTagMap.clear();
        {
            ItemStack itemStack = new ItemStack(Material.NAME_TAG);
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE+"玩家的默认称号~");
            assert itemMeta != null;
            itemMeta.setLore(lore);
            itemMeta.setDisplayName(Setting.nameTagDefault);
            itemStack.setItemMeta(itemMeta);
            NameTag nameTag = new NameTag("default", itemStack);
            registerNameTag(nameTag);
            defaultNameTag = nameTag;
        }
        Collection<NameTag> nameTags = getNameTagFromSQL();
        for(NameTag nameTag : nameTags) nameTagMap.put(nameTag.getKey(), nameTag);
        NameTagMain.info("已重载"+ChatColor.GREEN+nameTagMap.size()+ChatColor.WHITE+"个称号");
    }

    public static NameTag getDefaultNameTag() {
        return defaultNameTag;
    }

    public static boolean isDefaultNameTag(String key){
        return defaultNameTag.getKey().equals(key);
    }

    public static boolean containsNameTag(String key) {
        return nameTagMap.containsKey(key);
    }

    public static boolean containsSQLNameTag(String key) {
        return IOManager.getInstance().existData(nameTagTable, key);
    }

    public static void registerNameTag(NameTag nameTag) {
        nameTagMap.put(nameTag.getKey(), nameTag);
    }

    public static void deleteNameTag(String key) {
        IOManager.getInstance().update("DELETE FROM "+nameTagTable+" WHERE `key` = '"+key+"';");
        nameTagMap.remove(key);
    }

    public static boolean containsIllegalCharacters(String key){
        return key.contains(";") || key.equals("default");
    }

    public static boolean isIllegalLength(String key){
        return key.length() > 16;
    }

    public static boolean createNameTag(String key, ItemStack itemStack) {
        if(key == null){
            NameTagMain.warning("创建称号失败，称号iD为null");
            return false;
        }
        if(isIllegalLength(key)){
            NameTagMain.warning("创建称号失败，称号iD的长度过长");
            return false;
        }
        if(containsIllegalCharacters(key)){
            NameTagMain.warning("创建称号失败，称号iD中含有非法字符");
            return false;
        }
        if(itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            if(itemMeta.hasDisplayName()){
                String data = getDataFromItemStack(itemStack);
                if(!containsSQLNameTag(key)){
                    IOManager ioManager = IOManager.getInstance();
                    ioManager.update("INSERT INTO "+nameTagTable+" (`key`, `itemstack`) VALUES ('"+key+"', '"+data+"');");
                    NameTag nameTag = new NameTag(key, itemStack);
                    registerNameTag(nameTag);
                    return true;
                }else{
                    NameTagMain.warning("创建称号失败，已存在此称号");
                }
            }
        }
        return false;
    }

    public static NameTag getNameTag(String key) {
        if(key == null) return null;
        return nameTagMap.get(key);
    }

    public static Collection<NameTag> getNameTags() {
        return new ArrayList<>(nameTagMap.values());
    }

    public static String getDataFromItemStack(ItemStack itemStack) {
        StreamSerializer streamSerializer = new StreamSerializer();
        try {
            return streamSerializer.serializeItemStack(itemStack);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public static ItemStack getItemStackFromData(String data) {
        StreamSerializer streamSerializer = new StreamSerializer();
        try {
            return streamSerializer.deserializeItemStack(data);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }
}
