package GUI;

import javax.swing.*;

public class Add_Sale {
    public static void Add_Sale() {
        JFrame frame = new JFrame();
        frame.setTitle("Add Sale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 250);

        JLabel ProductName = new JLabel("Product Name: ");
        JLabel quantity = new JLabel("Quantity sold: ");
        JLabel Date = new JLabel("Date of Sale: ");

        JComboBox Product = new JComboBox();
        for (int i = 0; i < smartshop.InventoryManager.getProducts().size(); i++) {
            Product.addItem(smartshop.InventoryManager.getProducts().get(i).getName());
        }

        JTextField ProductQuantity = new JTextField(15);
        JTextField SaleDate = new JTextField(15);

        JButton Add = new JButton("Add sale");


        ProductName.setBounds(10, 10, 100, 20);
        Product.setBounds(100, 10, 300, 30);

        quantity.setBounds(10, 50, 100, 20);
        ProductQuantity.setBounds(100, 50, 300, 20);

        Date.setBounds(10, 80, 250, 20);
        SaleDate.setBounds(100, 80, 300, 20);

        Add.setBounds(10, 130, 100, 20);

        frame.add(ProductName);
        frame.add(Product);

        frame.add(quantity);
        frame.add(ProductQuantity);


        frame.add(Date);
        frame.add(SaleDate);

        frame.add(Add);

        frame.setVisible(true);

    }
}
