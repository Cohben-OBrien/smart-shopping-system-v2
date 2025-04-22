package GUI;

import Database.Data;
import Product.Product;
import User.User;
import User.User_authenticator;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class Main extends JFrame {

    static InventoryManager manager = new InventoryManager();
    static JFrame frame; // Make the frame a static member to access it in the ActionListener

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

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordStr = new String(password);

                try {
                    if (User_authenticator.User_Authemticator(username, passwordStr)) {
                        dispose(); // Close the login dialog
                        try {
                            frame = createAndShowGUI(); // Launch the main GUI and store the frame
                        } catch (SQLException ex) {
                            ex.printStackTrace(); // Print stack trace for debugging SQL exceptions
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); // Clear password field on failure
                    }
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage()); // Print the error message for SQL exceptions
                }
            });

            pack();
            setVisible(true);
        }
    }

    public static void Add_product_to_table(Product product) {
        Product.tableModel.addRow(new Object[]{product.getId(),product.getName(),product.getPrice(),product.getQuantity()});
    }

    public static JFrame createAndShowGUI() throws SQLException {
        manager.loadInventory(); // Load inventory data from the database

        manager.itemTable = new JTable(Product.tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int stockColumn = 3; // Index of the stock levels column
                int statusColumn = 4; // Index of the stock status column
                int priceColumn = 2; // Index of the price column

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                // Format the price column to display currency
                if (column == priceColumn) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Number) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK); // Use UK locale for currency format
                        setValueAt(currencyFormat.format(value), row, column);
                    }
                }

                // Set background color and status text based on stock levels
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

                        // Center the text in the status column header
                        TableCellRenderer headerRenderer = getColumnModel().getColumn(column).getHeaderRenderer();
                        if (headerRenderer == null) {
                            headerRenderer = getTableHeader().getDefaultRenderer();
                        }
                        if (c instanceof JLabel) {
                            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        }
                    } catch (NumberFormatException e) {
                        setValueAt("Error", row, column);
                        c.setBackground(Color.LIGHT_GRAY); // Indicate an error in parsing stock quantity
                    }
                } else if (column == stockColumn) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        manager.itemTable.setDefaultEditor(Object.class, null); // Make the table non-editable
        manager.itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        manager.itemTable.setRowSelectionAllowed(true); // Enable row selection

        frame = new JFrame("Smart Shopping System v1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 700);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Smart Shopping System v1");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLUE);
        contentPane.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonSearchPanel = new JPanel(new BorderLayout(5, 5)); // Panel to hold buttons and search bar
        buttonSearchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); // Add some padding

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Panel to hold the main action buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Add bottom padding

        JButton productsButton = new JButton("Add Products");
        productsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        productsButton.addActionListener(e -> {
            New_Item item = new New_Item();
            try {
                item.newItem(manager); // Open the "Add New Product" dialog
            } catch (SQLException a) {
                // Handle potential SQL exception during adding new item
            }
        });
        buttonPanel.add(productsButton);

        JButton recordSaleButton = new JButton("Record Sale");
        recordSaleButton.setFont(new Font("Arial", Font.PLAIN, 16));
        recordSaleButton.addActionListener(e -> {
            Add_Sale.Add_Sale(manager); // Open the "Record Sale" dialog
        });
        buttonPanel.add(recordSaleButton);

        JButton salesReportButton = new JButton("Sales Report");
        salesReportButton.setFont(new Font("Arial", Font.PLAIN, 16));
        salesReportButton.addActionListener(e -> {
            Sales_Report report = new Sales_Report();
            try {
                report.Sales_Report(); // Open the "Sales Report" window
            } catch (SQLException a) {
                System.out.println(a); // Print any SQL exception that occurs
            }
        });
        buttonPanel.add(salesReportButton);

        JButton lowStockButton = new JButton("Stock Report");
        lowStockButton.setFont(new Font("Arial", Font.PLAIN, 16));
        lowStockButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Low Stock Report functionality.")); // Placeholder for low stock report functionality
        buttonPanel.add(lowStockButton);

        // *** ADD DELETE BUTTON HERE ***
        JButton deleteProductButton = new JButton("Delete Product");
        deleteProductButton.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteProductButton.setEnabled(false); // Initially disabled
        deleteProductButton.addActionListener(e -> {
          
            int selectedRow = manager.itemTable.getSelectedRow();
            if(User_authenticator.getCurrent_user().getAccessLevel() == User.access_levels.ADMIN) {
                if (selectedRow != -1) {
                    int modelRow = manager.itemTable.convertRowIndexToModel(selectedRow); // Get the actual row index in the model
                    String productNameToDelete = (String) Product.tableModel.getValueAt(modelRow, 1); // Assuming product name is in the second column

                    int confirmation = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to delete '" + productNameToDelete + "'?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION  // Show Yes/No options
                    );

                    if (confirmation == JOptionPane.YES_OPTION) {
                        Product productToDelete = manager.findProduct(productNameToDelete);
                        if (productToDelete != null) {
                            try {
                                manager.removeProduct(productToDelete); // Use the existing removeProduct method
                                Product.tableModel.removeRow(modelRow); // Remove from the table model
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "Error deleting product from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace(); // Print stack trace for debugging
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);

                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a product to delete.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }else {
               JOptionPane.showMessageDialog(frame, "you need to be a admin to delete a product");
            }

        });
        buttonPanel.add(deleteProductButton);
        // *** END OF DELETE BUTTON ADDITION ***

        buttonSearchPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Panel for the search label and text field
        JLabel searchLabel = new JLabel("Product Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        searchPanel.add(searchLabel);

        JTextField searchTextField = new JTextField(30);
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        searchPanel.add(searchTextField);

        buttonSearchPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel buttonContainerPanel = new JPanel(new BorderLayout()); // Container for buttons and search
        buttonContainerPanel.add(buttonSearchPanel, BorderLayout.NORTH);

        String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels", "Stock Status"};
        Product.tableModel.setColumnCount(columnNames.length);
        Product.tableModel.setColumnIdentifiers(columnNames);

        try {
            manager.render_data(); // Populate the table with data from the database
        } catch (SQLException a) {
            // Handle potential SQL exception during data rendering
        }

        manager.itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(manager.itemTable); // Add the table to a scroll pane

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(Product.tableModel); // Create a sorter for the table
        manager.itemTable.setRowSorter(sorter); // Apply the sorter to the table

        // Add listener for the search text field to filter the table
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchTextField.getText();
                if (searchText.trim().isEmpty()) {
                    sorter.setRowFilter(null); // Show all rows if the search text is empty
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Case-insensitive search
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout()); // Panel to hold the scrollable table
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the table
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        buttonContainerPanel.add(tablePanel, BorderLayout.CENTER);
        contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the exit button
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(e -> System.exit(0)); // Exit the application
        exitPanel.add(exitButton);

        contentPane.add(exitPanel, BorderLayout.SOUTH);

        // Enable/disable delete button based on row selection in the table
        manager.itemTable.getSelectionModel().addListSelectionListener(event -> {
            deleteProductButton.setEnabled(manager.itemTable.getSelectedRow() != -1); // Enable if a row is selected, disable otherwise
        });

        // Double-click functionality to edit an item
        manager.itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && manager.itemTable.getSelectedRow() != -1) {

                    if(User_authenticator.getCurrent_user().getAccessLevel() == User.access_levels.ADMIN) {
                         int selectedRow = manager.itemTable.getSelectedRow();
                         String productName = manager.itemTable.getValueAt(selectedRow, 1).toString(); // Get the product name from the selected row
                          Product product = manager.findProduct(productName); // Find the product object using the name
                        try {
                            Edit_Item.editItem(manager, product); // Open the "Edit Item" dialog
      
                        } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error loading product"); // Show error message if loading fails
                        e.printStackTrace(); // Print stack trace for debugging
                        }

                    } else {
                        JOptionPane.showMessageDialog(frame, "You need to be a admin to edit/8 a product");

                    }

                }
            }
        });

        frame.setVisible(true); // Make the main frame visible
        return frame; // Return the created JFrame
    }
}