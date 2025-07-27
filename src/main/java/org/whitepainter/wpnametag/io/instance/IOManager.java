package org.whitepainter.wpnametag.io.instance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.whitepainter.wpnametag.NameTagMain;
import org.whitepainter.wpnametag.Setting;
import org.whitepainter.wpnametag.io.data.NameTag;
import org.whitepainter.wpnametag.io.data.PlayerData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public abstract class IOManager {
    private static IOManager ioManager = null;
    private final String database;

    private static final Map<String, PlayerData> playerDataMap = new HashMap<>();

    public IOManager(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public static IOManager getInstance() {
        return ioManager;
    }

    public abstract void onInit();

    public abstract void onShutdown();

    public static void init(){
        if(IOManager.ioManager != null) return;
        IOManager ioManager;
        if(Setting.enableMySQL){
            ioManager = new MySQLIO(Setting.hostname, String.valueOf(Setting.port), Setting.database, Setting.username, Setting.password);
        }else{
            ioManager = new SQLiteIO(Setting.database);
        }
        IOManager.ioManager = ioManager;
        ioManager.onInit();
        int Interval = 12000;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> list = new ArrayList<>(playerDataMap.keySet());
                for(String key : list) savePlayerData(key);
            }
        }.runTaskTimerAsynchronously(NameTagMain.getInstance(), Interval, Interval);
    }

    protected abstract Connection open() throws SQLException;

    public Connection getConnection(){
        Connection con = null;
        try {
            con = open();
        } catch (SQLException exception) {
            exception.fillInStackTrace();
        }
        if(con == null){
            NameTagMain.warning("无法连接到数据库，正在关闭服务器……");
            Bukkit.shutdown();
        }
        return con;
    }

    public void update(String command) {
        Connection con = getConnection();
        Statement st = null;
        try {
            st = con.createStatement();
            st.executeUpdate(command);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(String command) {
        Connection con = getConnection();
        ResultSet rs = null;
        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(command);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    public boolean existData(String table, String key){
        try {
            ResultSet rs = query("SELECT EXISTS (SELECT * FROM "+table+" WHERE `key`='"+key+"') AS flag;");
            if(rs != null && rs.next()) return (rs.getInt("flag") >= 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public abstract boolean existTable(String table);

    public void createBasicTable(String table, String columns) {
        createTable(table, "`key` VARCHAR(16), "+columns+", PRIMARY KEY (`key`)");
    }

    public void createTable(String table, String columns){
        if(!existTable(table)){
            update("CREATE TABLE " + table + " (" + columns + ");");
        }
    }

    public static PlayerData getPlayerData(String key){
        return playerDataMap.get(key);
    }

    public static boolean loadPlayerData(Player player) throws SQLException {
        String key = player.getName();
        if(playerDataMap.containsKey(key)) return false;
        IOManager ioManager = getInstance();
        ResultSet rs = ioManager.query("SELECT * FROM "+Setting.nameTagTablePlayer+" WHERE `key`='"+key+"';");
        PlayerData playerData;
        if(rs.next()){
            String used = rs.getString("used");
            String ownS = rs.getString("own");
            String[] ownedStrings = ownS.split(";");
            playerData = new PlayerData(key, used, Arrays.asList(ownedStrings));
            playerData.setInsert(false);
        }else{
            playerData = new PlayerData(key);
            playerData.setInsert(true);
        }
        playerDataMap.put(key, playerData);
        return true;
    }

    public static void savePlayerData(String key){
        PlayerData playerData = IOManager.getPlayerData(key);
        if(playerData == null) return;
        NameTag usedNameTag = playerData.getUsedNameTag();
        String used = usedNameTag != null ? usedNameTag.getKey() : "";
        String owned = String.join(";", playerData.getOwnedNameTagKey());
        IOManager ioManager = getInstance();
        if(playerData.isInsert()){
            ioManager.update("INSERT INTO "+Setting.nameTagTablePlayer+" (`key`,`used`,`own`) VALUES ('"+key+"','"+used+"','"+owned+"');");
        }else{
            ioManager.update("UPDATE "+Setting.nameTagTablePlayer+" SET `used`='"+used+"',`own`='"+owned+"' WHERE `key`='"+key+"';");
        }
    }

    public static void freePlayerData(String key){
        playerDataMap.remove(key);
    }
}
