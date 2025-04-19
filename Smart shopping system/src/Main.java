package GUI;

import Database.Data;
import Product.Product;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.color.ProfileDataException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class Main extends JFrame {

    static InventoryManager manager = new InventoryManager();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginDialog(); // Create and show the login dialog
        });
    }

    static class LoginDialog extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public LoginDialog() {
            setTitle("Log In");
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close only the dialog
            setModal(true); // Make it modal, preventing interaction with the background
            setLayout(new GridLayout(3, 2, 15,15));// Simple layout
            setPreferredSize(new Dimension(400,200));
            setLocationRelativeTo(null); // Center on screen

            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField(15);
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField(15);
            loginButton = new JButton("Log In");

            add(usernameLabel);
            add(usernameField);
            add(passwordLabel);
            add(passwordField);
            add(new JLabel()); // Empty label for spacing
            add(loginButton);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    char[] password = passwordField.getPassword();
                    String passwordStr = new String(password);

                    // **Replace with your actual authentication logic**
                    if (authenticateUser(username, passwordStr)) {
                        dispose(); // Close the login dialog
                        try {
                            createAndShowGUI(); // Launch the main GUI
                        } catch (SQLException ex) {

                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); // Clear password field on failure
                    }
                }
            });

            pack();
            setVisible(true);
        }

        // **Simple hardcoded authentication for demonstration**
        private boolean authenticateUser(String username, String password) {
            return username.equals("1234") && password.equals("1234");
        }
    }
    public static void Add_product_to_table(Product product) {
        Product.tableModel.addRow(new Object[]{product.getId(),product.getName(),product.getPrice(),product.getQuantity()});
    }


    public static void createAndShowGUI() throws SQLException {
        manager.loadInventory();

        manager.itemTable = new JTable(Product.tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int stockColumn = 3;
                int statusColumn = 4;
                int priceColumn = 2;

                // Always use white background unless we explicitly change it
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK); // Default foreground

                // Format the Price column with £ symbol
                if (column == priceColumn) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Number) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
                        setValueAt(currencyFormat.format(value), row, column);
                    }
                }

                // Set background colour and text for the Stock Status column
                if (column == statusColumn) {
                    Object stockValue = getValueAt(row, stockColumn);
                    try {
                        int quantity = Integer.parseInt(stockValue.toString());
                        String statusText;
                        Color backgroundColor;

                        if (quantity == 0) {
                            backgroundColor = new Color(255, 99, 71); // Red
                            statusText = "Out of Stock";
                        } else if (quantity < 10) {
                            backgroundColor = new Color(255, 255, 150); // Light yellow
                            statusText = "Low Stock";
                        } else if (quantity < 30) {
                            backgroundColor = new Color(255, 200, 0); // Orange
                            statusText = "Medium Stock";
                        } else {
                            backgroundColor = new Color(144, 238, 144); // Light green
                            statusText = "High Stock";
                        }
                        c.setBackground(backgroundColor);
                        setValueAt(statusText, row, column);

                        // Get the default renderer for the column and set alignment
                        TableCellRenderer headerRenderer = getColumnModel().getColumn(column).getHeaderRenderer();
                        if (headerRenderer == null) {
                            headerRenderer = getTableHeader().getDefaultRenderer();
                        }
                        if (c instanceof JLabel) {
                            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        }
                    } catch (NumberFormatException e) {
                        setValueAt("Error", row, column);
                        c.setBackground(Color.LIGHT_GRAY);
                    }
                }
                // Ensure stock level column still shows text on white background
                else if (column == stockColumn) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        };

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Shopping System v1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 700); // Adjusted width to accommodate the new column
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
            productsButton.addActionListener(e -> {
                New_Item item = new New_Item();

                try {
                    item.newItem(manager);
                } catch (SQLException a) {}

            });
            buttonPanel.add(productsButton);

            JButton recordSaleButton = new JButton("Record Sale");
            recordSaleButton.setFont(new Font("Arial", Font.PLAIN, 16));

            recordSaleButton.addActionListener(e -> {
                Add_Sale.Add_Sale(manager);

            });

            buttonPanel.add(recordSaleButton);

            JButton salesReportButton = new JButton("Sales Report");
            salesReportButton.setFont(new Font("Arial", Font.PLAIN, 16));
            salesReportButton.addActionListener(e -> {
                Sales_Report report = new Sales_Report();
                try {
                    report.Sales_Report();
                    manager.Update_Product(manager.getProducts().getLast(), "beer", 5, 100);
                } catch (SQLException a) {
                    System.out.println(a);
                }
            });
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

            // Modified column names with the new "Stock Status" column
            String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels", "Stock Status"};

            Product.tableModel.setColumnCount(columnNames.length);
            Product.tableModel.setColumnIdentifiers(columnNames);


            // JTable with price formatting and stock colour rules in the new column

            //load data to the table
            try {
                manager.render_data();
            } catch (SQLException a) {}


            manager.itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(manager.itemTable);

            // Search feature
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(Product.tableModel);
            manager.itemTable.setRowSorter(sorter);

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