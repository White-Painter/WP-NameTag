package org.whitepainter.wpnametag.io.instance;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.whitepainter.wpnametag.NameTagMain;
import org.whitepainter.wpnametag.Setting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteIO extends IOManager {
    private HikariDataSource ds;
    private Connection connection = null;

    public SQLiteIO(String database) {
        super(database);
    }

    @Override
    public void onInit() {
        HikariConfig config = new HikariConfig();
        String path = NameTagMain.getInstance().getDataFolder().getAbsolutePath().replace("\\", "/");
        config.setJdbcUrl("jdbc:sqlite:"+path+"/"+Setting.database +".db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setConnectionInitSql("PRAGMA journal_mode=WAL;");
        config.setMaximumPoolSize(1);
        this.ds = new HikariDataSource(config);
    }

    @Override
    public void onShutdown() {
        ds = null;
        try {
            connection.close();
        } catch (SQLException exception) {
            exception.fillInStackTrace();
        }
    }

    @Override
    protected Connection open() throws SQLException {
        if(connection == null) connection = ds.getConnection();
        return connection;
    }

    @Override
    public boolean existTable(String table) {
        ResultSet rs = query("SELECT COUNT(*) FROM sqlite_master WHERE type=\"table\" AND name=\""+table+"\";");
        try {
            if(rs.next()) return (rs.getInt("COUNT(*)") >= 1);
        } catch (SQLException exception) {
            exception.fillInStackTrace();
        }
        return false;
    }
}
