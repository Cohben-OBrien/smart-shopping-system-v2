package GUI;

import javax.swing.*;

public class New_Item {
    public void newItem() {
        JFrame frame = new JFrame("New Item");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);

        JLabel Item_name_label = new JLabel("New Item name");
        JTextField Item_name_textField = new JTextField(15);

        Item_name_label.setBounds(2, 20, 20, 20);
        Item_name_textField.setBounds(2, 45, 20, 20);


        frame.add(Item_name_label);


        frame.setVisible(true);

    }
}
