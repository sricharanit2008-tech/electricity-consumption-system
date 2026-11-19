package com.electricity.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

/**
 * Manages Database Connections.
 * Supports SQLite, MySQL, or falls back gracefully to local file/memory store
 * if external JDBC drivers are not present in the runtime classpath.
 */
public class DBConnection {
    // Default SQLite connection URL
    private static final String SQLITE_RELATIVE_PATH = "../database/electricity.db";
    private static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";

    // Optional MySQL connection settings (can be configured)
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/electricity_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "password";

    private static boolean driverAvailable = false;
    private static boolean checkedDriver = false;

    static {
        checkDrivers();
    }

    private static void checkDrivers() {
        if (checkedDriver) return;
        try {
            // Check for SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            driverAvailable = true;
            System.out.println("[DBConnection] SQLite JDBC Driver detected.");
        } catch (ClassNotFoundException e1) {
            try {
                // Check for MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverAvailable = true;
                System.out.println("[DBConnection] MySQL JDBC Driver detected.");
            } catch (ClassNotFoundException e2) {
                driverAvailable = false;
                System.out.println("[DBConnection] Note: No external JDBC driver detected in classpath.");
                System.out.println("[DBConnection] Using built-in local persistence engine (Zero dependencies needed).");
            }
        }
        checkedDriver = true;
    }

    public static boolean isDriverAvailable() {
        return driverAvailable;
    }

    public static Connection getConnection() throws SQLException {
        if (!driverAvailable) {
            return null;
        }

        try {
            // Locate sqlite file
            File dbFile = new File(SQLITE_RELATIVE_PATH);
            String dbAbsolutePath = dbFile.exists() ? dbFile.getAbsolutePath() : "electricity.db";
            return DriverManager.getConnection(SQLITE_URL_PREFIX + dbAbsolutePath);
        } catch (SQLException e) {
            // Try MySQL fallback if SQLite file fails
            try {
                return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
            } catch (SQLException ex) {
                throw e;
            }
        }
    }
}
