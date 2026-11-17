package com.mealmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Initialize Database
        try {
            Database.initialize();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null, "Database Error: " + e.getMessage()
            );
            return;
        }

        // Main Window
        JFrame frame = new JFrame("Meal Manager");
        frame.setSize(700, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(
            new GridLayout(8, 1, 10, 10)
        );

        JLabel heading = new JLabel(
            "MEAL MANAGER", SwingConstants.CENTER
        );

        heading.setFont(
            new Font("Arial", Font.BOLD, 28)
        );

        // Buttons
        JButton addButton = new JButton("Add Recipe");
        JButton viewButton = new JButton("View Recipes");
        JButton searchButton = new JButton("Search Recipes");
        JButton plannerButton = new JButton("Weekly Planner");
        JButton shoppingButton = new JButton("Shopping List");
        JButton deleteButton = new JButton("Delete Recipe");
        JButton exitButton = new JButton("Exit");

        // Button Actions
        addButton.addActionListener(
            e -> openAddRecipeForm()
        );

        viewButton.addActionListener(
            e -> viewRecipes()
        );

        searchButton.addActionListener(
            e -> searchRecipes()
        );

        plannerButton.addActionListener(e ->
            JOptionPane.showMessageDialog(
                frame, "Weekly Planner - Coming Soon!"
            )
        );

        shoppingButton.addActionListener(e ->
            JOptionPane.showMessageDialog(
                frame, "Shopping List - Coming Soon!"
            )
        );

        deleteButton.addActionListener(
            e -> deleteRecipe()
        );

        exitButton.addActionListener(
            e -> System.exit(0)
        );

        // Add Components
        panel.add(heading);
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(searchButton);
        panel.add(plannerButton);
        panel.add(shoppingButton);
        panel.add(deleteButton);
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    // ADD RECIPE FORM
    public static void openAddRecipeForm() {

        JFrame form = new JFrame("Add New Recipe");
        form.setSize(600, 500);
        form.setLocationRelativeTo(null);

        JPanel panel = new JPanel(
            new GridLayout(6, 2, 10, 10)
        );

        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();

        JTextArea ingredientsArea = new JTextArea(4, 20);
        JTextArea instructionsArea = new JTextArea(4, 20);

        JTextField servingsField = new JTextField();

        JButton saveButton = new JButton("Save Recipe");

        panel.add(new JLabel("Recipe Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Ingredients:"));
        panel.add(new JScrollPane(ingredientsArea));

        panel.add(new JLabel("Instructions:"));
        panel.add(new JScrollPane(instructionsArea));

        panel.add(new JLabel("Servings:"));
        panel.add(servingsField);

        panel.add(new JLabel(""));
        panel.add(saveButton);

        // SAVE RECIPE
        saveButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String ingredients = ingredientsArea.getText().trim();
            String instructions = instructionsArea.getText().trim();
            String servingsText = servingsField.getText().trim();

            if (name.isEmpty() || category.isEmpty()
                    || ingredients.isEmpty()
                    || instructions.isEmpty()
                    || servingsText.isEmpty()) {

                JOptionPane.showMessageDialog(
                    form, "Please fill in all fields!"
                );
                return;
            }

            try {

                int servings = Integer.parseInt(servingsText);

                if (servings <= 0) {
                    JOptionPane.showMessageDialog(
                        form, "Servings must be greater than zero!"
                    );
                    return;
                }

                RecipeDAO.saveRecipe(
                    name, category, ingredients,
                    instructions, servings
                );

                JOptionPane.showMessageDialog(
                    form, "Recipe Saved Successfully!"
                );

                form.dispose();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    form, "Please enter a valid servings number!"
                );

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                    form, "Database Error: " + ex.getMessage()
                );
            }
        });

        form.add(panel);
        form.setVisible(true);
    }

    // VIEW ALL RECIPES
    public static void viewRecipes() {

        try {

            List<String[]> recipes =
                RecipeDAO.getAllRecipes();

            showRecipeTable(recipes, "All Recipes");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null, "Database Error: " + e.getMessage()
            );
        }
    }

    // SEARCH RECIPES
    public static void searchRecipes() {

        String keyword = JOptionPane.showInputDialog(
            null, "Enter Recipe Name to Search:"
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        try {

            List<String[]> recipes =
                RecipeDAO.searchRecipes(keyword.trim());

            if (recipes.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null, "No Recipe Found!"
                );
                return;
            }

            showRecipeTable(recipes, "Search Results");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null, "Search Error: " + e.getMessage()
            );
        }
    }

    // DISPLAY RECIPE TABLE
    public static void showRecipeTable(
        List<String[]> recipes,
        String title
    ) {

        String[] columns = {
            "ID",
            "Recipe Name",
            "Category",
            "Ingredients",
            "Instructions",
            "Servings"
        };

        DefaultTableModel model =
            new DefaultTableModel(columns, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        for (String[] recipe : recipes) {
            model.addRow(recipe);
        }

        JTable table = new JTable(model);

        table.setAutoResizeMode(
            JTable.AUTO_RESIZE_OFF
        );

        JScrollPane scrollPane = new JScrollPane(table);

        JFrame resultFrame = new JFrame(title);

        resultFrame.setSize(900, 400);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.add(scrollPane);
        resultFrame.setVisible(true);
    }

    // DELETE RECIPE
    public static void deleteRecipe() {

        String input = JOptionPane.showInputDialog(
            null, "Enter Recipe ID to Delete:"
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {

            int id = Integer.parseInt(input.trim());

            if (id <= 0) {
                JOptionPane.showMessageDialog(
                    null, "Please enter a valid Recipe ID!"
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete Recipe ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean deleted = RecipeDAO.deleteRecipe(id);

            if (deleted) {

                JOptionPane.showMessageDialog(
                    null, "Recipe Deleted Successfully!"
                );

            } else {

                JOptionPane.showMessageDialog(
                    null, "Recipe ID Not Found!"
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                null, "Please enter a valid number!"
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                null, "Database Error: " + e.getMessage()
            );
        }
    }
}