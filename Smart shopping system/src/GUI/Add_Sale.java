package GUI;

import Product.Product;
import Records.ProductSale;
import manager.InventoryManager;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Add_Sale {

    static ArrayList<ProductSale> products = new ArrayList<>();

    public static DefaultTableModel ProductModel = new DefaultTableModel();

    private static void remove_Product(Product product, int row) {
        ProductModel.removeRow(row);
        for (int i = 0; i < products.size(); i++) {
            if(products.get(i).getProduct().getId() == product.getId()) {
                products.remove(i);
            }
        }
    }

    private static void add_table_product(Product product, int quantity) {
        ProductModel.addRow(new Object[]{product.getId(), product.getName(), quantity, String.format("£%.2f", product.getPrice()), String.format("£%.2f", product.getPrice() * quantity)});


    }

    private static void add_product(InventoryManager manager) {
        JFrame frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 250);

        JLabel ProductName = new JLabel("Product Name: ");
        JLabel quantity = new JLabel("Quantity sold: ");

        JComboBox ProductSelect = new JComboBox();
        for (int i = 0; i < InventoryManager.getProducts().size(); i++) {
            ProductSelect.addItem(InventoryManager.getProducts().get(i).getName().replace("_", " "));
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
            Product product = manager.findProduct(ProductSelect.getSelectedItem().toString());

            try {
                if (Database.Data.check_stock(product.getId(), Integer.valueOf(ProductQuantity.getText()))) {
                    products.add(new ProductSale(product, Integer.parseInt(ProductQuantity.getText())));
                    add_table_product(product, Integer.parseInt(ProductQuantity.getText()));
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Product does not have enough stock");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid quantity");
            }



        });

        frame.setVisible(true);


    }

    public static void Add_Sale(InventoryManager manager) {
        products.clear();
        ProductModel.setRowCount(0);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JTable productTable = new JTable();
        productTable.setModel(ProductModel);

        String[] columnNames = {"Product ID", "Product", "Quantity", "Price", "Total"};

        ProductModel.setColumnCount(columnNames.length);
        ProductModel.setColumnIdentifiers(columnNames);


        JScrollPane ProductTable = new JScrollPane(productTable);

        frame.setTitle("Add Sale");
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setSize(800, 800);

        ProductTable.setBounds(0, 0, 800, 700);
        frame.add(ProductTable, BorderLayout.CENTER);
        productTable.getColumnModel().getColumn(0).setResizable(true);
        productTable.getColumnModel().getColumn(1).setResizable(true);
        productTable.getColumnModel().getColumn(2).setResizable(true);
        productTable.getColumnModel().getColumn(3).setResizable(true);
        JLabel Date = new JLabel("Date of Sale: ");
        JXDatePicker SaleDate = new JXDatePicker();

        JButton add_product = new JButton("add product");
        JButton Add_sale = new JButton("Add sale");
        JButton remove_product = new JButton("remove product");

        add_product.setBounds(0, 720, 110, 20);
        remove_product.setBounds(110, 720, 150, 20);
        Add_sale.setBounds(260, 720, 100, 20);
        Date.setBounds(10, 700, 250, 20);
        SaleDate.setBounds(100, 700, 350, 20);

        frame.add(add_product);
        frame.add(remove_product);
        frame.add(Date);
        frame.add(SaleDate);

        add_product.addActionListener(e -> {
            System.out.println(products.size());
            add_product(manager);
            System.out.println(products.size());
        });

        remove_product.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            String product_name = productTable.getValueAt(selectedRow, 1).toString();
            remove_Product(manager.findProduct(product_name), selectedRow);

        });

        frame.add(Add_sale);
        frame.add(Add_sale);

        frame.setVisible(true);

        Add_sale.addActionListener(e ->{
            try {
                //InventoryManager.recordSale(products, SaleDate.getText());
                frame.dispose();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

    }
}
