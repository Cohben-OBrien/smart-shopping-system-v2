package smartshop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Step 1: Create an InventoryManager instance (handles all business logic like adding products, recording sales, etc.)
        InventoryManager manager = new InventoryManager();

        // Step 2: Set up the main application window (JFrame)
        JFrame frame = new JFrame("Smart Shop System");  // Title of the window
        frame.setSize(600, 400);  // Set the window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the app when the window is closed
        frame.setLayout(new BorderLayout());  // Set the layout for the window components

        // Step 3: Create a panel to hold buttons and input fields
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());  // Layout for buttons (horizontal)

        // Step 4: Create text fields for product name, price, and quantity
        JTextField productNameField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField quantityField = new JTextField(10);

        // Step 5: Create buttons for different actions
        JButton addProductButton = new JButton("Add Product");
        JButton recordSaleButton = new JButton("Record Sale");
        JButton showSalesButton = new JButton("Show Sales Report");
        JButton lowStockButton = new JButton("Show Low Stock Report");

        // Step 6: Add the text fields and buttons to the panel
        buttonPanel.add(new JLabel("Product Name:"));
        buttonPanel.add(productNameField);
        buttonPanel.add(new JLabel("Price:"));
        buttonPanel.add(priceField);
        buttonPanel.add(new JLabel("Quantity:"));
        buttonPanel.add(quantityField);
        buttonPanel.add(addProductButton);
        buttonPanel.add(recordSaleButton);
        buttonPanel.add(showSalesButton);
        buttonPanel.add(lowStockButton);

        // Step 7: Create a table to display products
        String[] columnNames = {"Product", "Price", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);  // Model for the table
        JTable productTable = new JTable(tableModel);  // Create the table
        JScrollPane scrollPane = new JScrollPane(productTable);  // Add scrolling functionality to the table
        frame.add(scrollPane, BorderLayout.CENTER);  // Add the table to the center of the window

        // Step 8: Action for the "Add Product" button
        addProductButton.addActionListener(e -> {
            String name = productNameField.getText();  // Get the product name
            float price = Float.parseFloat(priceField.getText());  // Get the price of the product
            int quantity = Integer.parseInt(quantityField.getText());  // Get the quantity of the product

            // Add the new product to the inventory
            Product newProduct = new Product(name, price, quantity);
            manager.addProduct(newProduct);

            // Add the new product to the table
            tableModel.addRow(new Object[]{name, price, quantity});

            // Clear the input fields after adding the product
            productNameField.setText("");
            priceField.setText("");
            quantityField.setText("");
        });

        // Step 9: Action for the "Record Sale" button
        recordSaleButton.addActionListener(e -> {
            String saleName = JOptionPane.showInputDialog("Enter product name for sale:");  // Ask for product name
            int saleQty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to sell:"));  // Ask for quantity to sell
            String date = JOptionPane.showInputDialog("Enter date (dd/mm/yyyy):");  // Ask for sale date

            // Record the sale if there is enough stock
            boolean success = manager.recordSale(saleName, saleQty, date);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Sale recorded!");  // Show success message
            } else {
                JOptionPane.showMessageDialog(frame, "Sale failed (check stock or name).");  // Show error message
            }
        });

        // Step 10: Action for the "Show Sales Report" button
        showSalesButton.addActionListener(e -> {
            // Create the sales report and display it in the text area
            String report = "---- SALES REPORT ----\n";
            List<SalesRecord> sales = manager.getSales();  // Get all sales records
            for (SalesRecord record : sales) {
                report += record.getDate() + " - " + record.getProduct().getName() + " - " +
                        record.getQuantity() + " units - £" + record.getTotalPrice() + "\n";  // Add sale details
            }
            // Show the sales report in a message box
            JOptionPane.showMessageDialog(frame, report);
        });

        // Step 11: Action for the "Show Low Stock Report" button
        lowStockButton.addActionListener(e -> {
            // Create the low stock report and display it in the text area
            String report = "---- LOW STOCK REPORT ----\n";
            List<Product> lowStockProducts = manager.getLowStockProducts();  // Get low stock products
            for (Product product : lowStockProducts) {
                report += product.getName() + " → only " + product.getQuantity() + " left.\n";  // Show product with low stock
            }
            // Show the low stock report in a message box
            JOptionPane.showMessageDialog(frame, report);
        });

        // Step 12: Make the window visible
        frame.setVisible(true);
    }
}























