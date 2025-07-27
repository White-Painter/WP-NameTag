package org.whitepainter.wpnametag.io.instance;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLIO extends IOManager {
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private HikariDataSource ds;

    public MySQLIO(String host, String port, String database, String user, String password) {
        super(database);
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void onInit() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://"+getHost()+":/"+getDatabase());
        config.setUsername(getUser());
        config.setPassword(getPassword());
        config.setMaximumPoolSize(10);
        this.ds = new HikariDataSource(config);
    }

    @Override
    public void onShutdown() {

    }

    @Override
    protected Connection open() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public boolean existTable(String table) {
        ResultSet rs = query("SELECT COUNT(*) FROM information_schema.TABLES WHERE table_name ='"+table+"';");
        try {
            if(rs.next()) return (rs.getInt(1) >= 1);
        } catch (SQLException exception) {
            exception.fillInStackTrace();
        }
        return false;
    }
}
