package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class newMain extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Shopping System v1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setLocationRelativeTo(null);

            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());

            // 1. Title Label (at the very top - NORTH)
            JLabel titleLabel = new JLabel("Smart Shopping System v1");
            titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setForeground(Color.BLUE);
            contentPane.add(titleLabel, BorderLayout.NORTH);

            // 2. Panel for Buttons and Search (below the title - NORTH of buttonContainerPanel)
            JPanel buttonSearchPanel = new JPanel(new BorderLayout(5, 5)); // Add some spacing
            buttonSearchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Reduced top padding
            JButton productsButton = new JButton("Add Products");
            productsButton.setFont(new Font("Arial", Font.PLAIN, 16));
            productsButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Add Products functionality."));
            buttonPanel.add(productsButton);

            JButton recordSaleButton = new JButton("Record Sale");
            recordSaleButton.setFont(new Font("Arial", Font.PLAIN, 16));
            recordSaleButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Record Sale functionality."));
            buttonPanel.add(recordSaleButton);

            JButton salesReportButton = new JButton("Sales Report");
            salesReportButton.setFont(new Font("Arial", Font.PLAIN, 16));
            salesReportButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Sales Report functionality."));
            buttonPanel.add(salesReportButton);

            JButton lowStockButton = new JButton("Stock Report");
            lowStockButton.setFont(new Font("Arial", Font.PLAIN, 16));
            lowStockButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Low Stock Report functionality."));
            buttonPanel.add(lowStockButton);

            buttonSearchPanel.add(buttonPanel, BorderLayout.NORTH);

            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Align text to the left
            JLabel searchLabel = new JLabel("Product Search:");
            searchLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            searchPanel.add(searchLabel);
            JTextField searchTextField = new JTextField(30);
            searchTextField.setFont(new Font("Arial", Font.PLAIN, 20));
            searchPanel.add(searchTextField);
            buttonSearchPanel.add(searchPanel, BorderLayout.SOUTH);

            JPanel buttonContainerPanel = new JPanel(new BorderLayout());
            buttonContainerPanel.add(buttonSearchPanel, BorderLayout.NORTH); // Buttons and search at the top

            // 3. Middle Panel for Table (below the buttons - CENTER of buttonContainerPanel)
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Sample table data
            String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels"};
            Object[][] data = {
                    {"101", "Boxers", 10.00, 10},
                    {"102", "Socks", 2.00, 50},
                    {"103", "T-Shirt", 15.00, 30},
                    {"104", "Blue Jeans", 35.00, 20},
                    {"105", "Cotton Socks", 3.50, 75}
            };

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            JTable itemTable = new JTable(tableModel);
            itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(itemTable);

            // Implement Table Row Sorter for Filtering
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            itemTable.setRowSorter(sorter);

            // Add Key Listener to the Search Text Field
            searchTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = searchTextField.getText();
                    if (searchText.trim().length() == 0) {
                        sorter.setRowFilter(null); // Show all rows when search is empty
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Case-insensitive filter
                    }
                }
            });

            tablePanel.add(scrollPane, BorderLayout.CENTER);
            buttonContainerPanel.add(tablePanel, BorderLayout.CENTER);

            contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

            // 4. Bottom Panel for Exit Button (at the very bottom - SOUTH)
            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton exitButton = new JButton("Exit");
            exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
            exitButton.addActionListener(e -> System.exit(0));
            exitPanel.add(exitButton);

            contentPane.add(exitPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}