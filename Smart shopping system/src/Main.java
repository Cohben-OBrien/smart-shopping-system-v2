package smartshop;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// This is the main class where everything runs (both logic and GUI).
public class Main {

    // Main method that starts the application
    public static void main(String[] args) {
        // Create the InventoryManager instance (handles business logic)
        InventoryManager manager = new InventoryManager();

        // Add some sample products to the inventory (for testing purposes)
        manager.addProduct(new smartshop.Product("Socks", 12.0f, 24));  // Product name, price, and quantity
        manager.addProduct(new smartshop.Product("Boxers", 25.0f, 6));  // Another sample product

        // Set up the main application window (JFrame)
        JFrame frame = new JFrame("Smart Shop System");  // Title of the window
        frame.setSize(600, 400);  // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the window on exit
        frame.setLayout(new BorderLayout());  // Layout for components in the window

        // Create a panel to hold buttons for interacting with the system
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());  // Layout for buttons (horizontal)

        // Create buttons for different actions
        JButton addProductButton = new JButton("Add Product");  // Button to add a product
        JButton recordSaleButton = new JButton("Record Sale");  // Button to record a sale
        JButton showSalesButton = new JButton("Show Sales Report");  // Button to view sales report
        JButton lowStockButton = new JButton("Show Low Stock Report");  // Button to view low stock report

        // Add buttons to the panel
        buttonPanel.add(addProductButton);
        buttonPanel.add(recordSaleButton);
        buttonPanel.add(showSalesButton);
        buttonPanel.add(lowStockButton);

        // Add the button panel to the top part of the window
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Create a text area where the reports will be displayed
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);  // Make it read-only (can't edit the report)
        JScrollPane scrollPane = new JScrollPane(reportArea);  // Add scroll functionality
        frame.add(scrollPane, BorderLayout.CENTER);  // Add the scrollable area to the center of the window

        // Action for the "Add Product" button
        addProductButton.addActionListener(e -> {
            // Ask the user to input product details (name, price, and quantity)
            String name = JOptionPane.showInputDialog("Enter product name:");
            String price = JOptionPane.showInputDialog("Enter product price:");
            String quantity = JOptionPane.showInputDialog("Enter product quantity:");

            // Add the product to the inventory
            manager.addProduct(new smartshop.Product(name, Float.parseFloat(price), Integer.parseInt(quantity)));
        });

        // Action for the "Record Sale" button
        recordSaleButton.addActionListener(e -> {
            // Ask the user to input sale details (product name, quantity, and date)
            String saleName = JOptionPane.showInputDialog("Enter product name for sale:");
            String saleQty = JOptionPane.showInputDialog("Enter quantity to sell:");
            String date = JOptionPane.showInputDialog("Enter date (dd/mm/yyyy):");

            // Record the sale (check if enough stock is available)
            boolean success = manager.recordSale(saleName, Integer.parseInt(saleQty), date);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Sale recorded!");  // Display success message
            } else {
                JOptionPane.showMessageDialog(frame, "Sale failed (check stock or name).");  // Display error message
            }
        });

        // Action for the "Show Sales Report" button
        showSalesButton.addActionListener(e -> {
            // Create a report for all sales made
            String report = "---- SALES REPORT ----\n";
            List<smartshop.SalesRecord> sales = manager.getSales();  // Get the list of sales
            for (smartshop.SalesRecord record : sales) {
                report += record.getDate() + " - " + record.getProduct().getName() + " - " +
                        record.getQuantity() + " units - £" + record.getTotalPrice() + "\n";
            }
            // Display the sales report in the text area
            reportArea.setText(report);
        });

        // Action for the "Show Low Stock Report" button
        lowStockButton.addActionListener(e -> {
            // Create a report for products with low stock (less than 5 units)
            String report = "---- LOW STOCK REPORT ----\n";
            List<smartshop.Product> lowStockProducts = manager.getLowStockProducts();  // Get low stock products
            for (smartshop.Product product : lowStockProducts) {
                report += product.getName() + " → only " + product.getQuantity() + " left.\n";
            }
            // Display the low stock report in the text area
            reportArea.setText(report);
        });

        // Make the frame visible to the user (show the window)
        frame.setVisible(true);
    }
}






















