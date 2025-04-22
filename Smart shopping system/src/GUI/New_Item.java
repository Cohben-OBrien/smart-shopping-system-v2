package GUI;

import Product.Product;
import manager.InventoryManager;

import javax.swing.*;
import java.sql.SQLException;

public class New_Item {

    // Opens a window to create and add a new product
    public void newItem(InventoryManager manager) throws SQLException {
        JFrame frame = new JFrame("New Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 200);

        // Labels and input fields for product name and price
        JLabel Item_name_label = new JLabel("New Item name");
        JTextField Item_name_textField = new JTextField(15);

        JLabel Item_price_label = new JLabel("New Item price");
        JTextField Item_price_textField = new JTextField(15);

        JLabel Item_quantity_label = new JLabel("Item quantity");
        JTextField Item_quantity_textField = new JTextField(15);

        // Set positions of the labels and text fields
        Item_name_label.setBounds(2, 20, 200, 20);
        Item_name_textField.setBounds(100, 20, 340, 20);

        Item_price_label.setBounds(2, 50, 200, 20);
        Item_price_textField.setBounds(100, 50, 340, 20);

        Item_quantity_label.setBounds(2, 70, 200, 20);
        Item_quantity_textField.setBounds(100, 70, 340, 20);

        // Add button to create the item
        JButton Add_item_button = new JButton("Add Item");
        Add_item_button.setBounds(200, 90, 80, 20);

        // Add components to the frame
        frame.add(Item_name_label);
        frame.add(Item_name_textField);
        frame.add(Item_price_label);
        frame.add(Item_price_textField);
        frame.add(Item_quantity_label);
        frame.add(Item_quantity_textField);
        frame.add(Add_item_button);

        frame.setVisible(true);

        // Handle button click to add the new item
        Add_item_button.addActionListener(e -> {
            String name = "";
            int quantity = 0;

            try {
                // Try to parse the price input
                float item_price = Float.parseFloat(Item_price_textField.getText());

                try {
                    // Try to parse the quantity input
                    quantity = Integer.parseInt(Item_quantity_textField.getText());

                    name = Item_name_textField.getText();

                    
                        if(item_price <= 0) {
                            JOptionPane.showMessageDialog(null, "Invalid price");
                        } else {
                            try {
                            // Create and add the new product
                     
                              manager.addProduct(new Product(InventoryManager.product_next_id(),name, item_price, quantity));
                            } catch (SQLException a ) {}

                        }

                      // Close the window after adding the item

                      frame.dispose();

                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a name for the item");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Quantity");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid price");
            }
        });
    }
}

