package org.whitepainter.wpnametag.io.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.whitepainter.wpnametag.NameTagManager;
import org.whitepainter.wpnametag.Setting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerData {
    private final String key;
    private String usedNameTagKey = null;
    private String temporaryNameTag = null;
    private final List<String> ownedNameTagKet = new ArrayList<>();
    private boolean insert = true;

    public PlayerData(String key) {
        this.key = key;
    }

    public PlayerData(String key, String usedNameTagKey, List<String> ownedNameTagKet) {
        this.key = key;
        this.usedNameTagKey = usedNameTagKey;
        this.ownedNameTagKet.addAll(ownedNameTagKet);
    }

    public String getKey() {
        return key;
    }

    public Collection<String> getOwnedNameTagKey(){
        return new ArrayList<>(ownedNameTagKet);
    }

    public Collection<NameTag> getOwnedNameTag(){
        List<NameTag> list = new ArrayList<>();
        for(String s : getOwnedNameTagKey()){
            NameTag nameTag = NameTagManager.getNameTag(s);
            if(nameTag != null) list.add(nameTag);
        }
        return list;
    }

    public boolean ownedNameTag(String nameTagKey){
        return ownedNameTagKet.contains(nameTagKey);
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public boolean canUsedNameTag(String nameTagKey){
        if(NameTagManager.isDefaultNameTag(nameTagKey) || ownedNameTag(nameTagKey)) return true;
        NameTag nameTag = NameTagManager.getNameTag(nameTagKey);
        Player player = Bukkit.getPlayer(getKey());
        if(nameTag != null && player != null) return player.hasPermission(nameTag.getPermission());
        return false;
    }

    public Collection<NameTag> getCanUsedNameTags(){
        List<NameTag> list = new ArrayList<>();
        for(NameTag nameTag : NameTagManager.getNameTags()){
            if(canUsedNameTag(nameTag.getKey())) list.add(nameTag);
        }
        return list;
    }

    public boolean setUsedNameTag(String nameTagKey){
        if(nameTagKey == null || canUsedNameTag(nameTagKey)){
            usedNameTagKey = nameTagKey;
            return true;
        }
        return false;
    }

    public NameTag getUsedNameTag(){
        NameTag nameTag = NameTagManager.getNameTag(usedNameTagKey);
        return nameTag != null ? nameTag : NameTagManager.getDefaultNameTag();
    }

    public String getUsedNameTagKey(){
        return getUsedNameTag().getKey();
    }

    public String getUsedNameTagFormat(){
        String temporaryNameTag = getTemporaryNameTag();
        if(temporaryNameTag != null) return Setting.nameTagPrefix+temporaryNameTag+Setting.nameTagSuffix;
        NameTag nameTag = getUsedNameTag();
        if(nameTag != null) return nameTag.getFormat();
        return "";
    }

    public String getTemporaryNameTag() {
        return temporaryNameTag;
    }

    public void setTemporaryNameTag(String temporaryNameTag) {
        if(temporaryNameTag == null){
            this.temporaryNameTag = null;
        }else{
            this.temporaryNameTag = temporaryNameTag.replace('&', ChatColor.COLOR_CHAR);
        }
    }

    public boolean giveNameTag(String nameTagKey){
        if(NameTagManager.containsNameTag(nameTagKey) && !ownedNameTag(nameTagKey) && !NameTagManager.isDefaultNameTag(nameTagKey)) return ownedNameTagKet.add(nameTagKey);
        return false;
    }

    public boolean removeNameTag(String nameTagKey){
        return ownedNameTagKet.remove(nameTagKey);
    }
}
