package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class newMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Shopping System v1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 650); // Increased height to accommodate the title
            frame.setLocationRelativeTo(null);

            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());

            // 1. Title Label (at the very top - NORTH)
            JLabel titleLabel = new JLabel("Smart Shopping System v1");
            titleLabel.setFont(new Font("Roboto", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPane.add(titleLabel, BorderLayout.NORTH);

            // 2. Panel for Buttons (below the title - CENTER of a new panel)
            JPanel buttonContainerPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton productsButton = new JButton("Add Products");
            productsButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Add Products functionality."));
            buttonPanel.add(productsButton);

            JButton recordSaleButton = new JButton("Record Sale");
            recordSaleButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Record Sale functionality."));
            buttonPanel.add(recordSaleButton);

            JButton salesReportButton = new JButton("Show Sales Report");
            salesReportButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Sales Report functionality."));
            buttonPanel.add(salesReportButton);

            JButton lowStockButton = new JButton("Show Low Stock Report");
            lowStockButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Low Stock Report functionality."));
            buttonPanel.add(lowStockButton);

            buttonContainerPanel.add(buttonPanel, BorderLayout.NORTH); // Buttons at the top of this container

            // 3. Middle Panel for Table (below the buttons - CENTER of the new panel)
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Sample table data
            String[] columnNames = {"Item ID", "Item Name", "Price", "Quantity"};
            Object[][] data = {
                    {"101", "Boxers", 10.00, 10},
                    {"102", "Socks", 2.00, 50},
                    {"103", "T-Shirt", 15.00, 30}
            };

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            JTable itemTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(itemTable);

            tablePanel.add(scrollPane, BorderLayout.CENTER);
            buttonContainerPanel.add(tablePanel, BorderLayout.CENTER);

            contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

            // 4. Bottom Panel for Exit Button (at the very bottom - SOUTH)
            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> System.exit(0));
            exitPanel.add(exitButton);

            contentPane.add(exitPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}