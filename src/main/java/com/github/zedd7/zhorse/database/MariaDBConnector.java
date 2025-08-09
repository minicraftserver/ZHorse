package com.github.zedd7.zhorse.database;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import com.github.zedd7.zhorse.ZHorse;

public class MariaDBConnector extends SQLDatabaseConnector {

       private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
       private static final String JDBC_OPTIONS = "?useSSL=true&autoReconnect=true&maxReconnects=10&failOverReadOnly=false";
       private static final String JDBC_URL = "jdbc:mariadb://%s:%d/%s" + JDBC_OPTIONS;

        private String host;
        private int port;
        private String user;
        private String password;
        private String name;
        private String url;

        public MariaDBConnector(ZHorse zh) {
                super(zh);
                host = zh.getCM().getDatabaseHost();
                port = zh.getCM().getDatabasePort();
                user = zh.getCM().getDatabaseUser();
                password = zh.getCM().getDatabasePassword();
                name = zh.getCM().getDatabaseName();
                tablePrefix = zh.getCM().getDatabaseTablePrefix();
               if (host != null && port != 0 && user != null && password != null && name != null && tablePrefix != null) {
                       url = String.format(JDBC_URL, host, port, name);
                       try {
                               openConnection();
                       } catch (SQLException e) {
                               zh.getLogger().severe(String.format("Failed to open connection with %s !", url));
                               zh.getLogger().severe("Verify that the database is created and that the user has access with given password.");
                               e.printStackTrace();
                               connected = false;
                       }
               }
               else {
                       zh.getLogger().severe("Could not connect to the database because your config is incomplete !");
               }
       }

        @Override
        public void openConnection() throws SQLException {
                try {
                        Class.forName(JDBC_DRIVER);
                } catch (ClassNotFoundException e) {
                        throw new SQLException("MariaDB JDBC driver not found", e);
                }

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(password);
                dataSource = new HikariDataSource(config);
                connected = true;
        }

}