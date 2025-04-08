package GUI;

import javax.swing.*;
import java.awt.event.ActionListener;

public class New_Item {
    public void newItem() {
        JFrame frame = new JFrame("New Item");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JButton Add_item_button = new JButton("Add Item");

        Add_item_button.setBounds(200, 90, 80, 20);


        frame.add(Item_name_label);
        frame.add(Item_name_textField);


        frame.add(Item_price_label);
        frame.add(Item_price_textField);

        frame.add(Item_quantity_label);
        frame.add(Item_quantity_textField);

        frame.add(Add_item_button);



        frame.setVisible(true);

    }
}
