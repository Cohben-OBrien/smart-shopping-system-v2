package GUI;

import smartshop.Product;
import smartshop.ProductData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Mainv3 {

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
            setLayout(new GridLayout(3, 2, 15,15));
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

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String passwordStr = new String(passwordField.getPassword());

                if (authenticateUser(username, passwordStr)) {
                    dispose(); // Close login dialog
                    createAndShowGUI(); // Launch the main GUI
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText(""); // Clear on error
                }
            });

            pack();
            setVisible(true);
        }

        // Simple demo login
        private boolean authenticateUser(String username, String password) {
            return username.equals("1234") && password.equals("1234");
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Smart Shopping System v1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 700);
        frame.setLocationRelativeTo(null);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Smart Shopping System v1");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLUE);
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Buttons and search bar
        JPanel buttonSearchPanel = new JPanel(new BorderLayout(5, 5));
        buttonSearchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

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

        // Fetch product list from ProductData class
        List<Product> products = ProductData.getInitialProducts();
        Object[][] data = new Object[products.size()][5];
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getName();
            data[i][2] = p.getPrice();
            data[i][3] = p.getQuantity();
            data[i][4] = ""; // Will be filled by renderer
        }

        String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels", "Stock Status"};

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable itemTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int stockColumn = 3;
                int statusColumn = 4;
                int priceColumn = 2;

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                // Format Price
                if (column == priceColumn) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Number) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
                        setValueAt(currencyFormat.format(value), row, column);
                    }
                }

                // Set Stock Status column formatting
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
                            backgroundColor = new Color(255, 255, 150); // Yellow
                            statusText = "Low Stock";
                        } else if (quantity < 30) {
                            backgroundColor = new Color(255, 200, 0); // Orange
                            statusText = "Medium Stock";
                        } else {
                            backgroundColor = new Color(144, 238, 144); // Green
                            statusText = "High Stock";
                        }

                        c.setBackground(backgroundColor);
                        setValueAt(statusText, row, column);

                        if (c instanceof JLabel) {
                            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        }
                    } catch (NumberFormatException e) {
                        setValueAt("Error", row, column);
                        c.setBackground(Color.LIGHT_GRAY);
                    }
                }

                return c;
            }
        };

        itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(itemTable);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchTextField.getText();
                if (searchText.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        buttonContainerPanel.add(tablePanel, BorderLayout.CENTER);
        contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

        // Exit button
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(e -> System.exit(0));
        exitPanel.add(exitButton);

        contentPane.add(exitPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
