package com.mealmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL =
        "jdbc:sqlite:mealmanager.db";

    public static Connection connect() throws SQLException {

        return DriverManager.getConnection(URL);
    }

    public static void initialize() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS recipes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                ingredients TEXT,
                instructions TEXT,
                servings INTEGER
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            System.out.println(
                "Database and Recipe Table created successfully!"
            );
        }
    }
}
