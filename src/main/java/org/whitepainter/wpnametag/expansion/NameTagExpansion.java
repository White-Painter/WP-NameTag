package org.whitepainter.wpnametag.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.whitepainter.wpnametag.NameTagMain;
import org.whitepainter.wpnametag.io.data.PlayerData;
import org.whitepainter.wpnametag.io.instance.IOManager;

public class NameTagExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "wpnametag";
    }

    @Override
    public @NotNull String getAuthor() {
        return "WhitePainter";
    }

    @Override
    public @NotNull String getVersion() {
        return NameTagMain.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if(offlinePlayer.isOnline()){
            Player player = (Player) offlinePlayer;
            String key = player.getName();
            String[] strings = params.split("_");
            int length = strings.length;
            PlayerData playerData = IOManager.getPlayerData(key);
            if(playerData != null){
                if(length >= 1){
                    if(strings[0].equals("used")){
                        if(length == 1){
                            return playerData.getUsedNameTagKey();
                        }else if(length == 2){
                            if(strings[1].equals("format")) return playerData.getUsedNameTagFormat();
                        }
                    }else if(length == 2 && strings[0].equals("owned") && strings[1].equals("amount")){
                            return String.valueOf(playerData.getOwnedNameTagKey().size());
                    }else if(strings[0].startsWith("owned:")){
                        return playerData.ownedNameTag(params.substring(6)) ? "true" : "false";
                    }
                }
            }
        }
        return super.onRequest(offlinePlayer, params);
    }
}
