package ru.itis.dis403.semestrovka.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static DataSource dataSource;
    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("Hepi_pro323");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        dataSource = new HikariDataSource(config);

    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }

    public static void destroyConnection(){
        ((HikariDataSource)dataSource).close();
    }
}
