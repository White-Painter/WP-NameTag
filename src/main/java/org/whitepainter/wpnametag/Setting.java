package org.whitepainter.wpnametag;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Setting {
    public static boolean enableMySQL;
    public static String hostname;
    public static int port;
    public static String database;
    public static String username;
    public static String password;
    public static String nameTagTableNameTag;
    public static String nameTagTablePlayer;
    public static boolean nameTagPermission;
    public static String nameTagDefault;
    public static String nameTagPrefix;
    public static String nameTagSuffix;

    public static void reload(){
        loadConfig(NameTagMain.getInstance().getConfig());
    }

    public static void loadConfig(FileConfiguration config) {
        enableMySQL = config.getBoolean("io.mysql");
        hostname = config.getString("io.sql.hostname");
        port = config.getInt("io.sql.port");
        database = config.getString("io.sql.database");
        username = config.getString("io.sql.username");
        password = config.getString("io.sql.password");
        nameTagTableNameTag = config.getString("io.table.nameTag", "nametags");
        nameTagTablePlayer = config.getString("io.table.player", "nametag_player_data");
        nameTagPermission = config.getBoolean("nameTags.permission", true);
        nameTagDefault = config.getString("nameTags.default", "&a萌新").replace('&', ChatColor.COLOR_CHAR);
        nameTagPrefix = config.getString("nameTags.prefix", "&f[").replace('&', ChatColor.COLOR_CHAR);
        nameTagSuffix = config.getString("nameTags.suffix", "&f]").replace('&', ChatColor.COLOR_CHAR);
    }
}
