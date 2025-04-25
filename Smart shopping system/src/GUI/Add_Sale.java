package GUI;

import Product.Product;
import Records.ProductSale;
import manager.InventoryManager;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Add_Sale {

    static ArrayList<ProductSale> products = new ArrayList<>();
    private static JLabel totalLabel;
    public static DefaultTableModel ProductModel = new DefaultTableModel();

    private static void remove_Product(Product product, int row) {
        ProductModel.removeRow(row);
        for (int i = 0; i < products.size(); i++) {
            if(products.get(i).getProduct().getId() == product.getId()) {
                products.remove(i);
            }
        }
    }

    private static void render_table() {
        ProductModel.setRowCount(0);
        for (int i = 0; i < products.size(); i++) {
            ProductModel.addRow(new Object[]{products.get(i).getProduct().getId(), products.get(i).getProduct().getName(), products.get(i).getQuantity(), String.format("£%.2f", products.get(i).getProduct().getPrice()),  String.format("£%.2f", products.get(i).getTotal())});
        }



    }

    private static void updateTotalLabel() {
        double total = 0;
        for (ProductSale sale : products) {
            total += sale.getQuantity() * sale.getProduct().getPrice();
        }
        totalLabel.setText("Total: " + String.format("%.2f", total));
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
                boolean update = false;
                if (Database.Data.check_stock(product.getId(), Integer.valueOf(ProductQuantity.getText()))) {
                    for(ProductSale sale: products) {
                        if(sale.getProduct() == product) {
                            sale.updatesale(Integer.parseInt(ProductQuantity.getText()));
                            System.out.println("same item");
                            update = true;
                        }
                    }
                    if(!update) {
                        products.add(new ProductSale(product, Integer.parseInt(ProductQuantity.getText())));
                    }

                    render_table();
                    updateTotalLabel();
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        JXDatePicker SaleDate = new JXDatePicker(new Date());
        SaleDate.setFormats("dd/MM/yyyy");

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

        totalLabel = new JLabel("Total: £0.00");
        totalLabel.setBounds(650, 700, 200, 30);
        frame.add(totalLabel);

        frame.setVisible(true);

        Add_sale.addActionListener(e ->{
            LocalDate date;
            if(products.size() == 0) {
                JOptionPane.showMessageDialog(frame, "Please add products to the sale");
            } else {
                try {
                    manager.recordSale(products, dateFormat.format(SaleDate.getDate()));
                    frame.dispose();
                } catch (Exception A) {
                    System.out.println(A);
                }
            }
        });

    }
}
