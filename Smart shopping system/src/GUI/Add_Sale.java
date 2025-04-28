package GUI;

import Product.Product;
import Records.ProductSale;
import manager.InventoryManager;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Add_Sale {

    static ArrayList<ProductSale> products = new ArrayList<>();
    private static JLabel totalLabel;
    public static DefaultTableModel ProductModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private static boolean saleuodate = false;

    private static void remove_Product(Product product, int row) {
        ProductModel.removeRow(row);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProduct().getId() == product.getId()) {
                products.remove(i);
            }
        }
    }

    private static void redner_table_data() {
        ProductModel.setRowCount(0);
        for(ProductSale productSale : products) {
            ProductModel.addRow(new Object[]{productSale.getProduct().getId(), productSale.getProduct().getName(), productSale.getQuantity(), String.format("£%.2f", productSale.getProduct().getPrice()), String.format("£%.2f", productSale.getProduct().getPrice() * productSale.getQuantity())});
        }
    }



    private static void updateTotalLabel() {
        double total = 0;
        for (ProductSale sale : products) {
            total += sale.getQuantity() * sale.getProduct().getPrice();
        }
        totalLabel.setText("Total: " + String.format("£%.2f", total));
    }


    private static void add_product(InventoryManager manager, JFrame parentFrame, JTable productTable) {
        JFrame frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(parentFrame);

        JLabel searchLabel = new JLabel("Search Product:");
        JTextField searchTextField = new JTextField(15);
        JLabel quantityLabel = new JLabel("Quantity sold:");
        JTextField quantityTextField = new JTextField(10);
        JComboBox<String> ProductSelect = new JComboBox<>();
        List<Product> allProducts = InventoryManager.getProducts();
        for (Product product : allProducts) {
            if(product.isSelling()) {
                ProductSelect.addItem(product.getName());
            }
            }
        JButton Add = new JButton("Add Product to Sale");

        frame.add(searchLabel);
        frame.add(searchTextField);
        frame.add(new JLabel("   "));
        frame.add(quantityLabel);
        frame.add(quantityTextField);
        frame.add(new JLabel("   "));
        frame.add(ProductSelect);
        frame.add(Add);

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchTerm = searchTextField.getText().trim().toLowerCase();
                ProductSelect.removeAllItems();
                for (Product product : allProducts) {
                    if (product.getName().toLowerCase().contains(searchTerm)) {
                        ProductSelect.addItem(product.getName().replace("_", " "));
                    }
                }
            }
        });

        Add.addActionListener(e -> {
            String selectedProductName = (String) ProductSelect.getSelectedItem();
            System.out.println(selectedProductName);
            Product product = manager.findProduct(selectedProductName);

            try {
                int qty = Integer.parseInt(quantityTextField.getText());
                try { // Catch SQLException here
                    if (Database.Data.check_stock(product.getId(), qty)) {
                        for(ProductSale productSale : products) {
                            if(productSale.getProduct().getId() == product.getId()) {
                                productSale.update(qty);
                                saleuodate = true;
                            }
                        }
                        if(!saleuodate) {
                            products.add(new ProductSale(product, qty));
                        }
                        redner_table_data();
                        updateTotalLabel();
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Product '" + selectedProductName + "' does not have enough stock");
                    }
                } catch (SQLException sqlException) {
                    JOptionPane.showMessageDialog(frame, "Database error checking stock: " + sqlException.getMessage());
                    sqlException.printStackTrace(); // For debugging
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid quantity");
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(frame, "Please select a product");
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    public static void Add_Sale(InventoryManager manager) {
        products.clear();
        ProductModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);

        JTable productTable = new JTable();
        productTable.setModel(ProductModel);

        String[] columnNames = {"Product ID", "Product", "Quantity", "Price", "Total"};
        ProductModel.setColumnCount(columnNames.length);
        ProductModel.setColumnIdentifiers(columnNames);

        JScrollPane ProductTableScrollPane = new JScrollPane(productTable);
        frame.add(ProductTableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add_productButton = new JButton("Add Product");
        JButton remove_productButton = new JButton("Remove Product");
        JLabel dateLabel = new JLabel("Date of Sale: ");
        JXDatePicker SaleDate = new JXDatePicker(new Date());
        SaleDate.setFormats(new SimpleDateFormat("dd/MM/yyyy", Locale.UK));
        JButton add_saleButton = new JButton("Add Sale");

        bottomPanel.add(add_productButton);
        bottomPanel.add(remove_productButton);
        bottomPanel.add(dateLabel);
        bottomPanel.add(SaleDate);
        bottomPanel.add(add_saleButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        add_productButton.addActionListener(e -> add_product(manager, frame, productTable));

        remove_productButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                String product_name = productTable.getValueAt(selectedRow, 1).toString();
                remove_Product(manager.findProduct(product_name.replace(" ", "_")), selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a product to remove");
            }
        });
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int selectedRow = productTable.getSelectedRow();
                String product_name = productTable.getValueAt(selectedRow, 1).toString();

                Product selectedProduct = manager.findProduct(product_name);
                String currentQuantity = productTable.getValueAt(selectedRow, 2).toString();
                String newQuantity = JOptionPane.showInputDialog(frame,"Enter new quantity for " + product_name + ":", currentQuantity);

                if (newQuantity != null) {
                    try {
                        int newQuantityInt = Integer.parseInt(newQuantity);
                        if (newQuantityInt <= 0) {
                            JOptionPane.showMessageDialog(frame, "Please enter a valid quantity");
                            return;
                        }
                        if (Database.Data.check_stock(selectedProduct.getId(), newQuantityInt)) {
                            System.out.println(productTable.getSelectedColumnCount());
                            for(ProductSale productSale : products) {
                                if(productSale.getProduct().getId() == selectedProduct.getId()) {
                                    productSale.setQuantity(newQuantityInt);
                                    break;
                                }
                            }
                            redner_table_data();
                            updateTotalLabel();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Product '" + selectedProduct.getName() + "' does not have enough stock");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid quantity");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Database error checking stock: " + ex.getMessage());
                    }
                }
            }
        });

        add_saleButton.addActionListener(e -> {
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please add products to the sale");
            } else {
                try {
                    manager.recordSale(products, dateFormat.format(SaleDate.getDate()));
                    frame.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error recording sale: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
        totalLabel = new JLabel("Total: £0.00");
        bottomPanel.add(totalLabel);

        frame.setVisible(true);

    }
}