import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTextArea reportArea;  // This will display the report
    private InventoryManager manager; // Store the manager for use in other parts

    public MainFrame(InventoryManager manager) {
        this.manager = manager;

        setTitle("Smart Shop System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JTextArea to display the report
        reportArea = new JTextArea();
        reportArea.setEditable(false);  // Make the text area non-editable
        JScrollPane scrollPane = new JScrollPane(reportArea);  // Add scrolling ability
        add(scrollPane, BorderLayout.CENTER);  // Add the scrollable text area to the frame

        // Create buttons for reports
        JButton showSalesReportButton = new JButton("Show Sales Report");
        showSalesReportButton.addActionListener(e -> generateSalesReport());  // Button click to generate sales report
        JButton showLowStockReportButton = new JButton("Show Low Stock Report");
        showLowStockReportButton.addActionListener(e -> generateLowStockReport()); // Button click to generate low stock report

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showSalesReportButton);
        buttonPanel.add(showLowStockReportButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Method to call the ReportGenerator and show the sales report
    public void generateSalesReport() {
        // Call the report generator to display the sales report
        smartshop.ReportGenerator.showSalesReport(manager.getSales(), reportArea);
    }

    // Method to call the ReportGenerator and show the low stock report
    public void generateLowStockReport() {
        // Call the report generator to display the low stock report
        smartshop.ReportGenerator.showLowStockReport(manager.getProducts(), 5, reportArea);
    }
}











