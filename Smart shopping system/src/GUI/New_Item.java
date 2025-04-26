package GUI;

import Product.Product;
import Product.Categories;
import Product.Product_Category;
import jdk.jfr.Category;
import manager.InventoryManager;

import javax.swing.*;
import java.awt.image.ImageProducer;
import java.sql.SQLException;

public class New_Item {
    public void newItem(InventoryManager manager) throws SQLException {
        JFrame frame = new JFrame("New Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 200);

        JLabel Item_name_label = new JLabel("New Item name");
        JTextField Item_name_textField = new JTextField(15);

        JLabel Item_price_label = new JLabel("New Item price");
        JTextField Item_price_textField = new JTextField(15);


        Item_name_label.setBounds(2, 20, 200, 20);
        Item_name_textField.setBounds(100, 20, 340, 20);

        Item_price_label.setBounds(2, 50, 200, 20);
        Item_price_textField.setBounds(100, 50, 340, 20);

        JLabel Item_quantity_label = new JLabel("Item quantity");
        JTextField Item_quantity_textField = new JTextField(15);

        Item_quantity_label.setBounds(2, 70, 200, 20);
        Item_quantity_textField.setBounds(100, 70, 340, 20);

        JLabel Item_Category_label = new JLabel("Item Category");
        Item_Category_label.setBounds(2, 90, 200, 20);

        JComboBox Category = new JComboBox();
        Category.setBounds(100, 90, 340, 20);


        JButton Add_item_button = new JButton("Add Item");
        Add_item_button.setBounds(100, 120, 100, 20);


        frame.add(Item_name_label);
        frame.add(Item_name_textField);


        frame.add(Item_price_label);
        frame.add(Item_price_textField);

        frame.add(Item_quantity_label);
        frame.add(Item_quantity_textField);
        frame.add(Item_Category_label);
        frame.add(Category);

        frame.add(Add_item_button);


        for(Product_Category category: Database.Data.LoadCategories()) {
            Category.addItem(category.getCategoryName());
        }


        frame.setVisible(true);

        Add_item_button.addActionListener(e -> {
            String name = "";
            int quantity = 0;

            try {
                float item_price = Float.parseFloat(Item_price_textField.getText());
                try {
                    quantity = Integer.parseInt(Item_quantity_textField.getText());

                    name = Item_name_textField.getText();
                    if (!name.isEmpty()) {
                        if(item_price <= 0) {
                            JOptionPane.showMessageDialog(null, "Invalid price");
                        } else {
                            try {
                                manager.addProduct(new Product(InventoryManager.product_next_id(),name, item_price, quantity, Categories.findCategory(Category.getSelectedItem().toString()), true));
                            } catch (Exception a ) {}

                        }
                       frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a name for the item");
                    }

                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Invalid Quantity");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid price");
            }


       });
    }
}
