package GUI;

import Records.ProductSale;

import javax.swing.*;
import java.util.ArrayList;

public class Add_Sale {

    static ArrayList<ProductSale> products = new ArrayList<>();

    public static void add_product(smartshop.InventoryManager manager) {

        JFrame frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 250);

        JLabel ProductName = new JLabel("Product Name: ");
        JLabel quantity = new JLabel("Quantity sold: ");

        JComboBox ProductSelect = new JComboBox();
        for (int i = 0; i < smartshop.InventoryManager.getProducts().size(); i++) {
            ProductSelect.addItem(smartshop.InventoryManager.getProducts().get(i).getName());
        }

        JTextField ProductQuantity = new JTextField(15);

        JButton Add = new JButton("Add product");


        ProductName.setBounds(10, 10, 100, 20);
        ProductSelect.setBounds(100, 10, 300, 30);

        quantity.setBounds(10, 50, 100, 20);
        ProductQuantity.setBounds(100, 50, 300, 20);

        Add.setBounds(10, 130, 100, 20);

        frame.add(ProductName);
        frame.add(ProductSelect);

        frame.add(quantity);
        frame.add(ProductQuantity);

        frame.add(Add);


        Add.addActionListener(e -> {
            smartshop.Product product = manager.findProduct(ProductSelect.getSelectedItem().toString());
            products.add(new ProductSale(product, Integer.parseInt(ProductQuantity.getText())));

            frame.dispose();

        });

        frame.setVisible(true);


    }

    public static void Add_Sale(smartshop.InventoryManager manager) {
        JFrame frame = new JFrame();





        frame.setTitle("Add Sale");
        frame.setLayout(null);
        frame.setSize(800, 800);


        JLabel Date = new JLabel("Date of Sale: ");
        JTextField SaleDate = new JTextField(15);

        JButton add_product = new JButton("add product");
        JButton Add_sale = new JButton("Add sale");

        add_product.setBounds(10, 720, 100, 20);
        Date.setBounds(10, 700, 250, 20);
        SaleDate.setBounds(100, 700, 350, 20);

        frame.add(add_product);
        frame.add(Date);
        frame.add(SaleDate);

        add_product.addActionListener(e -> {
            System.out.println(products.size());
            add_product(manager);
            System.out.println(products.size());
        });

        frame.add(Add_sale);

        frame.setVisible(true);

        Add_sale.addActionListener(e ->{

        });

    }
}
