package GUI;

import Product.Product;
import Product.Product_Category;
import manager.InventoryManager;
import Product.Categories;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;

public class New_Item {
    public void newItem(InventoryManager manager) {
        JFrame frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // New Item Name
        JLabel Item_name_label = new JLabel("New Item name");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(Item_name_label, gbc);
        JTextField Item_name_textField = new JTextField(15);
        gbc.gridx = 1;
        frame.add(Item_name_textField, gbc);

        // Item Price
        JLabel Item_price_label = new JLabel("New Item price");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(Item_price_label, gbc);
        JTextField Item_price_textField = new JTextField(15);
        gbc.gridx = 1;
        frame.add(Item_price_textField, gbc);

        // Item Quantity
        JLabel Item_quantity_label = new JLabel("Item quantity");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(Item_quantity_label, gbc);
        JTextField Item_quantity_textField = new JTextField(15);
        gbc.gridx = 1;
        frame.add(Item_quantity_textField, gbc);

        // Item Category
        JLabel Item_Category_label = new JLabel("Item Category");
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(Item_Category_label, gbc);
        JComboBox<String> Category_comboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        frame.add(Category_comboBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        try {
            Categories.LoadCategories();
            for (Product_Category category : Categories.GetCategories()) {
                Category_comboBox.addItem(category.getCategoryName());
            }
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // Consider disabling the category combo box or handling the error more gracefully
        }

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton Add_item_button = new JButton("Add Item");
        frame.add(Add_item_button, gbc);
        frame.getRootPane().setDefaultButton(Add_item_button);

        Add_item_button.addActionListener(e -> {
            String name = Item_name_textField.getText();
            String priceText = Item_price_textField.getText();
            String quantityText = Item_quantity_textField.getText();
            String selectedCategoryName = (String) Category_comboBox.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name for the item");
                return;
            }
            if (priceText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the item price");
                return;
            }
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the item quantity");
                return;
            }

            try {
                float item_price = Float.parseFloat(priceText);
                if (item_price <= 0) {
                    JOptionPane.showMessageDialog(null, "Invalid price");
                    return;
                }
                try {
                    int quantity = Integer.parseInt(quantityText);
                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(null, "Quantity cannot be negative");
                        return;
                    }

                    Product_Category category = Categories.findCategory(selectedCategoryName);
                    if (category == null) {
                        JOptionPane.showMessageDialog(null, "Selected category not found");
                        return;
                    }

                    Product newProduct = new Product(InventoryManager.product_next_id(), name, item_price, quantity, category, true);
                    try {
                        manager.addProduct(newProduct);
                        JOptionPane.showMessageDialog(null, "Product added successfully");
                        frame.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to add product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Quantity");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid price");
            }
        });

        frame.setSize(500, 250);
        frame.setVisible(true);
    }
}