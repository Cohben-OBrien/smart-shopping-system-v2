package GUI;

import Product.Product;
import Product.Product_Category;
import manager.InventoryManager;
import Product.Categories;
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

        JLabel Item_Category_label = new JLabel("Edit Item Category");
        Item_Category_label.setBounds(2, 90, 200, 20);

        nameLabel.setBounds(2, 20, 190, 20);
        nameField.setBounds(120, 20, 340, 20);

        priceLabel.setBounds(2, 50, 190, 20);
        priceField.setBounds(120, 50, 340, 20);

        quantityLabel.setBounds(2, 70, 190, 20);
        quantityField.setBounds(120, 70, 340, 20);

        JComboBox Category = new JComboBox();
        Category.setBounds(120, 90, 340, 20);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(100, 120, 100, 22);

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(priceLabel);
        frame.add(priceField);
        frame.add(quantityLabel);
        frame.add(quantityField);
        frame.add(updateButton);
        frame.add(Item_Category_label);
        frame.add(Category);

        for(Product_Category category: Database.Data.LoadCategories()) {
            Category.addItem(category.getCategoryName());
        }
        Category.setSelectedItem(product.getCategory().getCategoryName());
        frame.setVisible(true);

        updateButton.addActionListener(e -> {
            try {
                if((nameField.getText().equals("")) || (priceField.getText().equals("")) || (quantityField.getText().equals(""))){
                    JOptionPane.showMessageDialog(frame, "Please enter all the fields");
                } else {

                    String newName = nameField.getText().trim();
                    float newPrice = Float.parseFloat(priceField.getText());
                    int newQuantity = Integer.parseInt(quantityField.getText());

                    manager.Update_Product(product, newName, newPrice, newQuantity, Categories.findCategory(Category.getSelectedItem().toString()));
                    JOptionPane.showMessageDialog(frame, "Item updated successfully");
                    frame.dispose();
                }} catch (Exception a) {
                JOptionPane.showMessageDialog(frame, "Something went wrong");
                }

        });
    }
}
