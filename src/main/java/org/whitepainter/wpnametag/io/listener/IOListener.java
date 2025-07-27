package org.whitepainter.wpnametag.io.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.whitepainter.wpnametag.NameTagMain;
import org.whitepainter.wpnametag.io.instance.IOManager;

import java.sql.SQLException;

public class IOListener implements Listener {
    @EventHandler
    public void event(PlayerJoinEvent event){
        try {
            if(!IOManager.loadPlayerData(event.getPlayer())){
                NameTagMain.warning("初始化玩家'"+event.getPlayer().getName()+"'的称号数据时发生了异常，此玩家的数据已经被初始化过了！");
            }
        } catch (SQLException e) {
            NameTagMain.warning("初始化玩家'"+event.getPlayer().getName()+"'的称号数据失败");
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void event(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String key = player.getName();
        IOManager.savePlayerData(key);
        IOManager.freePlayerData(key);
    }
}
