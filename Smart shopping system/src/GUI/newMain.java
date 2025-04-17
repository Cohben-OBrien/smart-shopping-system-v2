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

            // Title label at the top
            JLabel titleLabel = new JLabel("Smart Shopping System v1");
            titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setForeground(Color.BLUE);
            contentPane.add(titleLabel, BorderLayout.NORTH);

            // Panel for buttons and search
            JPanel buttonSearchPanel = new JPanel(new BorderLayout(5, 5));
            buttonSearchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            // Action buttons
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

            // Search bar
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel searchLabel = new JLabel("Product Search:");
            searchLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            searchPanel.add(searchLabel);

            JTextField searchTextField = new JTextField(30);
            searchTextField.setFont(new Font("Arial", Font.PLAIN, 20));
            searchPanel.add(searchTextField);

            buttonSearchPanel.add(searchPanel, BorderLayout.SOUTH);

            JPanel buttonContainerPanel = new JPanel(new BorderLayout());
            buttonContainerPanel.add(buttonSearchPanel, BorderLayout.NORTH);

            // Sample table data
            String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels"};
            Object[][] data = {
                    {"101", "Boxers", 10.00, 10},
                    {"102", "Socks", 2.00, 0},
                    {"103", "T-Shirt", 15.00, 30},
                    {"104", "Blue Jeans", 35.00, 8},
                    {"105", "Cotton Socks", 3.50, 75}
            };

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

            // JTable with price formatting and stock color rules
            JTable itemTable = new JTable(tableModel) {
                @Override
                public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    int stockColumn = 3;
                    int priceColumn = 2;

                    // Always use white background unless we change it
                    c.setBackground(Color.WHITE);

                    // Format the Price column with £ symbol
                    if (column == priceColumn) {
                        Object value = getValueAt(row, column);
                        if (value instanceof Number) {
                            float price = ((Number) value).floatValue();
                            setValueAt("£" + String.format("%.2f", price), row, column);
                        }
                    }

                    // Change text color and background for Stock Levels
                    if (column == stockColumn) {
                        Object stockValue = getValueAt(row, stockColumn);
                        try {
                            int quantity = Integer.parseInt(stockValue.toString());
                            if (quantity == 0) {
                                c.setForeground(Color.RED); // Red for out of stock
                            } else {
                                c.setForeground(Color.BLACK);
                            }

                            if (quantity < 10 && quantity > 0) {
                                c.setBackground(new Color(255, 255, 150)); // Light yellow for low stock
                            }

                        } catch (NumberFormatException e) {
                            c.setForeground(Color.BLACK);
                            c.setBackground(Color.WHITE);
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setBackground(Color.WHITE);
                    }

                    return c;
                }
            };

            itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(itemTable);

            // Search feature
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            itemTable.setRowSorter(sorter);

            searchTextField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = searchTextField.getText();
                    if (searchText.trim().isEmpty()) {
                        sorter.setRowFilter(null); // Show all rows
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Case-insensitive search
                    }
                }
            });

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            tablePanel.add(scrollPane, BorderLayout.CENTER);

            buttonContainerPanel.add(tablePanel, BorderLayout.CENTER);
            contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

            // Exit button at the bottom
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