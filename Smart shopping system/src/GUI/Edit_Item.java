package GUI;

import Product.Product;
import manager.InventoryManager;

import javax.swing.*;
import java.sql.SQLException;


public class Edit_Item {
    public static void editItem(InventoryManager manager, Product product) throws SQLException {
        JFrame frame = new JFrame("Edit Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 200);

        JLabel nameLabel = new JLabel("Edit Item Name: ");
        JTextField nameField = new JTextField(product.getName());

        JLabel priceLabel = new JLabel("Edit price: ");
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));

        JLabel quantityLabel = new JLabel("Edit Item Quantity: ");
        JTextField quantityField = new JTextField(String.valueOf(product.getQuantity()));

        nameLabel.setBounds(2, 20, 200, 20);
        nameField.setBounds(100, 20, 340, 20);

        priceLabel.setBounds(2, 50, 200, 20);
        priceField.setBounds(100, 50, 340, 20);

        quantityLabel.setBounds(2, 70, 200, 20);
        quantityField.setBounds(100, 70, 340, 20);

        JButton updateButton = new JButton("Update Item");
        updateButton.setBounds(200, 100, 120, 25);

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(priceLabel);
        frame.add(priceField);
        frame.add(quantityLabel);
        frame.add(quantityField);
        frame.add(updateButton);

        frame.setVisible(true);

        updateButton.addActionListener(e -> {
            try {
                if((nameField.getText().equals("")) || (priceField.getText().equals("")) || (quantityField.getText().equals(""))){
                    JOptionPane.showMessageDialog(frame, "Please enter all the fields");
                } else {

                    String newName = nameField.getText().trim();
                    float newPrice = Float.parseFloat(priceField.getText());
                    int newQuantity = Integer.parseInt(quantityField.getText());

                    manager.Update_Product(product, newName, newPrice, newQuantity);
                    frame.dispose();
                }} catch (Exception a) {
                }

        });
    }
}
