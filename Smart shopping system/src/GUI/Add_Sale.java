package GUI;

import Product.Product;
import Records.ProductSale;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Add_Sale {

    // List to store the products included in the sale
    static ArrayList<ProductSale> products = new ArrayList<>();

    // Table model to display the sale details
    public static DefaultTableModel ProductModel = new DefaultTableModel();

    // Removes a product from the table and the list based on the product ID
    private static void remove_Product(Product product, int row) {
        ProductModel.removeRow(row);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProduct().getId() == product.getId()) {
                products.remove(i);
            }
        }
    }

    // Adds a product to the table with quantity and price details
    private static void add_table_product(Product product, int quantity) {
        ProductModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                quantity,
                String.format("£%.2f", product.getPrice()),
                String.format("£%.2f", product.getPrice() * quantity)
        });
    }

    // Opens a window to select a product and quantity to add to the sale
    private static void add_product(InventoryManager manager) {
        JFrame frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(500, 250);

        JLabel ProductName = new JLabel("Product Name: ");
        JLabel quantity = new JLabel("Quantity sold: ");

        // Dropdown with all product names
        JComboBox ProductSelect = new JComboBox();
        for (int i = 0; i < InventoryManager.getProducts().size(); i++) {
            ProductSelect.addItem(InventoryManager.getProducts().get(i).getName().replace("_", " "));
        }

        JTextField ProductQuantity = new JTextField(15);
        JButton Add = new JButton("Add product");

        // Set component positions
        ProductName.setBounds(10, 10, 100, 20);
        ProductSelect.setBounds(100, 10, 300, 30);
        quantity.setBounds(10, 50, 100, 20);
        ProductQuantity.setBounds(100, 50, 300, 20);
        Add.setBounds(10, 130, 100, 20);

        // Add components to the frame
        frame.add(ProductName);
        frame.add(ProductSelect);
        frame.add(quantity);
        frame.add(ProductQuantity);
        frame.add(Add);

        // Handle add button click
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

    // Main method to open the Add Sale window
    public static void Add_Sale(InventoryManager manager) {
        products.clear(); // Reset products list
        ProductModel.setRowCount(0); // Clear table rows

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable productTable = new JTable();
        productTable.setModel(ProductModel);

        // Define column headers
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

        // Allow resizing of columns
        productTable.getColumnModel().getColumn(0).setResizable(true);
        productTable.getColumnModel().getColumn(1).setResizable(true);
        productTable.getColumnModel().getColumn(2).setResizable(true);
        productTable.getColumnModel().getColumn(3).setResizable(true);

        JLabel Date = new JLabel("Date of Sale: ");
        JTextField SaleDate = new JTextField(15);

        JButton add_product = new JButton("add product");
        JButton Add_sale = new JButton("Add sale");
        JButton remove_product = new JButton("remove product");

        // Set positions of buttons and fields
        add_product.setBounds(0, 720, 110, 20);
        remove_product.setBounds(110, 720, 150, 20);
        Add_sale.setBounds(260, 720, 100, 20);
        Date.setBounds(10, 700, 250, 20);
        SaleDate.setBounds(100, 700, 350, 20);

        // Add buttons and fields to the frame
        frame.add(add_product);
        frame.add(remove_product);
        frame.add(Date);
        frame.add(SaleDate);
        frame.add(Add_sale); // Note: previously added twice

        // Add product button logic
        add_product.addActionListener(e -> {
            add_product(manager);
        });

        // Remove selected product from the list and table
        remove_product.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            String product_name = productTable.getValueAt(selectedRow, 1).toString();
            remove_Product(manager.findProduct(product_name), selectedRow);
        });

        // Record the sale and close the window
        Add_sale.addActionListener(e -> {
            try {
                InventoryManager.recordSale(products, SaleDate.getText());
                frame.dispose();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}



