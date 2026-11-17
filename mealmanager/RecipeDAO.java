package com.mealmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {

    // =================================
    // 1. SAVE RECIPE
    // =================================

    public static void saveRecipe(
        String name,
        String category,
        String ingredients,
        String instructions,
        int servings
    ) throws SQLException {

        String sql = "INSERT INTO recipes "
                + "(name, category, ingredients, instructions, servings) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setString(3, ingredients);
            pstmt.setString(4, instructions);
            pstmt.setInt(5, servings);

            pstmt.executeUpdate();

            System.out.println("Recipe Saved Successfully!");
        }
    }

    // =================================
    // 2. VIEW ALL RECIPES
    // =================================

    public static List<String[]> getAllRecipes()
            throws SQLException {

        List<String[]> recipes = new ArrayList<>();

        String sql = "SELECT * FROM recipes ORDER BY id DESC";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String[] recipe = {
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("ingredients"),
                    rs.getString("instructions"),
                    String.valueOf(rs.getInt("servings"))
                };

                recipes.add(recipe);
            }
        }

        return recipes;
    }

    // =================================
    // 3. SEARCH RECIPE
    // =================================

    public static List<String[]> searchRecipes(
        String keyword
    ) throws SQLException {

        List<String[]> recipes = new ArrayList<>();

        String sql = "SELECT * FROM recipes "
                   + "WHERE name LIKE ? "
                   + "ORDER BY id DESC";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    String[] recipe = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("ingredients"),
                        rs.getString("instructions"),
                        String.valueOf(rs.getInt("servings"))
                    };

                    recipes.add(recipe);
                }
            }
        }

        return recipes;
    }

    // =================================
    // 4. DELETE RECIPE
    // =================================

    public static boolean deleteRecipe(int id)
            throws SQLException {

        String sql = "DELETE FROM recipes WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0;
        }
    }

}