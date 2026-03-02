package com.revplay.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DbMigrationRunner.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking database schema for required columns...");

        tryAddColumn("ARTIST_ACCOUNT", "profile_image_url", "VARCHAR2(500)");
        tryAddColumn("ARTIST_ACCOUNT", "banner_image_url", "VARCHAR2(500)");
        tryAddColumn("ARTIST_ACCOUNT", "twitter_link", "VARCHAR2(255)");
        tryAddColumn("ARTIST_ACCOUNT", "website_link", "VARCHAR2(255)");

        log.info("Database schema check completed.");
    }

    private void tryAddColumn(String tableName, String columnName, String columnType) {
        try {
            // Check if column exists, if an exception is thrown it probably doesn't
            String checkSql = "SELECT " + columnName + " FROM " + tableName + " WHERE 1=0";
            jdbcTemplate.execute(checkSql);
            // If we are here, column exists
        } catch (Exception e) {
            // Column does not exist, let's add it
            try {
                String sql = "ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType;
                log.info("Executing: {}", sql);
                jdbcTemplate.execute(sql);
                log.info("Successfully added {} to {}", columnName, tableName);
            } catch (Exception ex) {
                log.warn("Failed to add column {} to {}: {}", columnName, tableName, ex.getMessage());
            }
        }
    }
}
