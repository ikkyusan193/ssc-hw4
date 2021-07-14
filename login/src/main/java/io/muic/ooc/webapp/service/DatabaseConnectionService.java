package io.muic.ooc.webapp.service;


import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;
import io.muic.ooc.webapp.config.ConfigProperties;
import io.muic.ooc.webapp.config.ConfigurationLoader;


public class DatabaseConnectionService {

    private final HikariDataSource ds;

    private static DatabaseConnectionService service;

    private DatabaseConnectionService(){
        ds = new HikariDataSource();
        ds.setMaximumPoolSize(20);
        ConfigProperties configProperties = ConfigurationLoader.load();
        if (configProperties == null){
            throw new RuntimeException("Unable to read config.properties");
        }
        ds.setDriverClassName(configProperties.getDatabaseDriverClassName());
        ds.setJdbcUrl(configProperties.getDatabaseConnectionUrl());
        ds.addDataSourceProperty("user","ssc");
        ds.addDataSourceProperty("password","secret");
        ds.setAutoCommit(false);
    }

    public Connection getConnection() throws SQLException{
        return ds.getConnection();
    }

    public static DatabaseConnectionService getInstance(){
        if(service == null){
            service = new DatabaseConnectionService();
        }
        return service;
    }

}
