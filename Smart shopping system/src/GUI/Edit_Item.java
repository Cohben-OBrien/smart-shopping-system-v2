package GUI;

import Product.Product;
import manager.InventoryManager;

import javax.swing.*;
import java.sql.SQLException;

public class Edit_Item {

    // Opens a window to edit a product's name, price, and quantity
    public static void editItem(InventoryManager manager, Product product) throws SQLException {
        JFrame frame = new JFrame("Edit Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 200);

        // Labels and input fields for the product details
        JLabel nameLabel = new JLabel("Edit Item Name: ");
        JTextField nameField = new JTextField(product.getName());

        JLabel priceLabel = new JLabel("Edit price: ");
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));

        JLabel quantityLabel = new JLabel("Edit Item Quantity: ");
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));

        // Set positions for each label and field
        nameLabel.setBounds(2, 20, 200, 20);
        nameField.setBounds(100, 20, 340, 20);

        priceLabel.setBounds(2, 50, 200, 20);
        priceField.setBounds(100, 50, 340, 20);

        quantityLabel.setBounds(2, 70, 200, 20);
        quantityField.setBounds(100, 70, 340, 20);

        // Button to save the updates
        JButton updateButton = new JButton("Update Item");
        updateButton.setBounds(200, 100, 120, 25);

        // Add all components to the frame
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(priceLabel);
        frame.add(priceField);
        frame.add(quantityLabel);
        frame.add(quantityField);
        frame.add(updateButton);

        frame.setVisible(true);

        // Handle the button click event
        updateButton.addActionListener(e -> {
            try {
                // Check if any field is empty
                if ((nameField.getText().equals("")) ||
                        (priceField.getText().equals("")) ||
                        (quantityField.getText().equals(""))) {

                    JOptionPane.showMessageDialog(frame, "Please enter all the fields");

                } else {
                    // Get updated values from input
                    String newName = nameField.getText().trim();
                    float newPrice = Float.parseFloat(priceField.getText());
                    int newQuantity = Integer.parseInt(quantityField.getText());

                    // Update the product using the manager
                    manager.Update_Product(product, newName, newPrice, newQuantity);

                    // Close the window after update
                    frame.dispose();
                }
            } catch (Exception a) {
                // Handle any errors silently (could be improved with logging)
            }
        });
    }
}

