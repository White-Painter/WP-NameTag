package org.whitepainter.wpnametag;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.whitepainter.wpnametag.command.NameTagCommand;
import org.whitepainter.wpnametag.expansion.NameTagExpansion;
import org.whitepainter.wpnametag.gui.GUIListener;
import org.whitepainter.wpnametag.io.instance.IOManager;
import org.whitepainter.wpnametag.io.listener.IOListener;

public final class NameTagMain extends JavaPlugin {
    private static NameTagMain instance;

    @Override
    public void onEnable() {
        instance = this;
        reloadConfig();
        IOManager.init();
        NameTagManager.init();
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new NameTagExpansion().register();
        Bukkit.getPluginManager().registerEvents(new IOListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getPluginCommand("nametag").setExecutor(new NameTagCommand());
        Bukkit.getPluginCommand("nametag").setTabCompleter(new NameTagCommand());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        Setting.reload();
    }

    public static NameTagMain getInstance(){
        return instance;
    }

    public static void info(String msg){
        NameTagMain.getInstance().getLogger().info(msg);
    }

    public static void warning(String msg){
        NameTagMain.getInstance().getLogger().warning(msg);
    }
}
