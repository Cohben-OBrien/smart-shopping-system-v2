package smartshop;

import GUI.Add_Sale;
import GUI.New_Item;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

// This is the main class where everything runs (both logic and GUI).
public class Main {

    // Main method that starts the application
    public static void main(String[] args) throws SQLException {
        // Create the InventoryManager instance (handles business logic)
        smartshop.InventoryManager manager = new smartshop.InventoryManager();

        // Add some sample products to the inventory (for testing purposes)

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
            New_Item item = new New_Item();
            item.newItem(manager);
        });


        // Action for the "Record Sale" button
        recordSaleButton.addActionListener(e -> {
            Add_Sale.Add_Sale();

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

        manager.loadInventory();
        // Make the frame visible to the user (show the window)
        frame.setVisible(true);
    }
}






















