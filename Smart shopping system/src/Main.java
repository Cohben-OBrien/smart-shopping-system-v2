package GUI;

import Database.Data;
import Product.Product;
import User.User;
import User.User.access_levels;
import User.User_authenticator;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * Main class for IntelliShop application
 */
public class Main extends JFrame {

    // Global variables and components
    static InventoryManager manager = new InventoryManager();
    static JFrame frame;
    static JButton deleteProductButton;
    static JButton undoDeleteButton;
    static Product lastDeletedProduct = null;
    static int lastDeletedRow = -1;
    static Timer undoTimer;
    static JPanel notificationPanel;
    static JLabel dateLabel;
    static JTextField searchTextField;
    static JPanel leftButtonPanel = new JPanel();
    static JLabel usernameLabelBottom;

    public static void main(String[] args) {
        // Launch the application with the login screen
        SwingUtilities.invokeLater(() -> {
            new LoginDialog();
        });
    }

    /**
     * Login dialog for user authentication
     */
    static class LoginDialog extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton exitButton;

        public LoginDialog() {
            setTitle("Log In");
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setModal(true);
            setLayout(new BorderLayout(15, 15));
            setPreferredSize(new Dimension(400, 200));
            setLocationRelativeTo(null);

            int fieldPadding = 5;
            int dialogMargin = 20;

            // Panel for username and password fields
            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(dialogMargin, dialogMargin, 10, dialogMargin));

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            usernameField = new JTextField(15);
            usernameField.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            passwordField = new JPasswordField(15);
            passwordField.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));

            inputPanel.add(usernameLabel);
            inputPanel.add(usernameField);
            inputPanel.add(passwordLabel);
            inputPanel.add(passwordField);

            // Panel for buttons (Login and Exit)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, dialogMargin, dialogMargin, dialogMargin));

            loginButton = new JButton("Log In");
            loginButton.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            Dimension buttonSize = new Dimension(120, 30);
            loginButton.setPreferredSize(buttonSize);

            exitButton = new JButton("Exit");
            exitButton.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            exitButton.setPreferredSize(buttonSize);

            buttonPanel.add(loginButton);
            buttonPanel.add(exitButton);

            getContentPane().add(inputPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            // Action listeners for login and exit buttons
            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordStr = new String(password);

                try {
                    if (User_authenticator.User_Authemticator(username, passwordStr)) {
                        dispose(); // Close login dialog
                        System.out.println("Login successful, calling createAndShowGUI with username: " + username);
                        frame = createAndShowGUI(username);
                        System.out.println("Returned from createAndShowGUI after login");
                    } else {
                        // Show error if credentials are invalid
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (SQLException ex) {
                    // Handle database errors
                    JOptionPane.showMessageDialog(LoginDialog.this, "A database error occurred during login: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    passwordField.setText("");
                } catch (Exception ex) {
                    // Handle general errors
                    JOptionPane.showMessageDialog(LoginDialog.this, "An error occurred during login: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    passwordField.setText("");
                }
            });

            // Exit the application
            exitButton.addActionListener(e -> {
                System.exit(0);
            });

            pack();
            setVisible(true);
        }
    }


    /**
     * Add a product to the table model
     */
    public static void Add_product_to_table(Product product) {
        Product.tableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getQuantity(),
                ""
        });
    }

    /**
     * Get the current date as a formatted string
     */
    private static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return currentDate.format(formatter);
    }

    /**
     * Get the current time as a formatted string
     */
    private static String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    /**
     * Start a live clock that updates the date and time every second
     */
    private static void startClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateLabel.setText(getCurrentDate() + "  " + getCurrentTime());
            }
        });
        timer.start();
    }

    /**
     * Create and display the main GUI after successful login
     */
    @SuppressWarnings({"squid:S1160"})
    public static JFrame createAndShowGUI(String username) throws SQLException, Exception {
        manager.loadInventory(); // Load inventory from database

        // Setup JTable with custom rendering for stock status and price formatting
        manager.itemTable = new JTable(Product.tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int stockColumn = 4;
                int statusColumn = 5;
                int priceColumn = 2;

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                // Format price column as currency
                if (column == priceColumn) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Number) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
                        setValueAt(currencyFormat.format(value), row, column);
                    }
                }

                // Color background of stock status based on quantity
                if (column == statusColumn) {
                    Object stockValue = getValueAt(row, stockColumn);
                    if (stockValue instanceof Number) {
                        int quantity = ((Number) stockValue).intValue();
                        String statusText;
                        Color backgroundColor;

                        if (quantity == 0) {
                            statusText = "Out of Stock";
                            backgroundColor = new Color(255, 99, 71);
                        } else if (quantity < 10) {
                            statusText = "Low Stock";
                            backgroundColor = new Color(255, 200, 0);
                        } else if (quantity < 30) {
                            statusText = "Medium Stock";
                            backgroundColor = new Color(255, 255, 150);
                        } else {
                            statusText = "In Stock";
                            backgroundColor = new Color(144, 238, 144);
                        }
                        c.setBackground(backgroundColor);
                        setValueAt(statusText, row, column);
                    } else {
                        setValueAt("Error", row, column);
                        c.setBackground(Color.LIGHT_GRAY);
                    }
                    c.setForeground(Color.BLACK);
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

        manager.itemTable.setDefaultEditor(Object.class, null); // Make table cells non-editable
        manager.itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        manager.itemTable.setRowSelectionAllowed(true);

        // Center align some table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Create main application frame
        frame = new JFrame("IntelliShop - Smart Shopping Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create panel to hold title with spacing
        JPanel titleAreaPanel = new JPanel(new BorderLayout());

        JLabel topSpacingLabel = new JLabel("");
        topSpacingLabel.setPreferredSize(new Dimension(0, 20));
        titleAreaPanel.add(topSpacingLabel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("IntelliShop - Smart Shopping Management");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleAreaPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel bottomSpacingLabel = new JLabel("");
        bottomSpacingLabel.setPreferredSize(new Dimension(0, 10));
        titleAreaPanel.add(bottomSpacingLabel, BorderLayout.SOUTH);


        // Left side panel for buttons and logo
        leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load and resize shopping cart icon
        java.net.URL imageUrl = Main.class.getResource("/resources/shopping_cart_icon.png");
        System.out.println("Image URL: " + imageUrl);
        ImageIcon originalIcon = new ImageIcon(imageUrl);
        Image originalImage = originalIcon.getImage();
        int scaledWidth = 75;
        int scaledHeight = 75;
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftButtonPanel.add(imageLabel);

        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space after logo

        // Buttons for actions
        JButton productsButton = new JButton("Add Product");
        deleteProductButton = new JButton("Delete");
        JButton recordSaleButton = new JButton("Record Sale");
        JButton salesReportButton = new JButton("Sales Report");
        JButton lowStockButton = new JButton("Stock Report");
        JButton exitButtonMain = new JButton("Log-out");

        // Set size and alignment for all buttons
        JButton[] buttons = {productsButton, deleteProductButton, recordSaleButton, salesReportButton, lowStockButton, exitButtonMain};
        int maxWidth = 0;
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            maxWidth = Math.max(maxWidth, button.getPreferredSize().width);
        }
        int padding = 20;
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            button.setMaximumSize(new Dimension(maxWidth + padding, button.getPreferredSize().height));
            leftButtonPanel.add(button);
            if (i < buttons.length - 1) {
                leftButtonPanel.add(Box.createVerticalGlue()); // Add vertical glue between buttons
            }
        }

        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space after last button

        // Button actions

        // Open Sales Report
        salesReportButton.addActionListener(e -> {
            Sales_Report report = new Sales_Report();
            try {
                report.Sales_Report();
            } catch (SQLException a) {
                System.out.println(a);
            }
        });

        // Open Stock Report
        lowStockButton.addActionListener(e -> {
            try {
                Stock_report.Stock_Report();
            } catch (SQLException a) {
                // Ignore
            }
        });

        // Open New Item window
        productsButton.addActionListener(e -> {
            New_Item item = new New_Item();
            try {
                item.newItem(manager);
            } catch (Exception a) {
                // Ignore
            }
        });

        // Open Record Sale window
        recordSaleButton.addActionListener(e -> {
            Add_Sale.Add_Sale(manager);
        });

        // Handle delete product action
        deleteProductButton.setEnabled(false);
        deleteProductButton.addActionListener(e -> {
            if (User_authenticator.getCurrent_user().getAccessLevel() == access_levels.ADMIN) {
                int selectedRow = manager.itemTable.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = manager.itemTable.convertRowIndexToModel(selectedRow);
                    String productNameToDelete = (String) Product.tableModel.getValueAt(modelRow, 1);

                    int confirmation = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to delete '" + productNameToDelete + "'?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirmation == JOptionPane.YES_OPTION) {
                        lastDeletedProduct = manager.findProduct(productNameToDelete);
                        if (lastDeletedProduct != null) {
                            lastDeletedRow = modelRow;
                            Product.tableModel.removeRow(modelRow);
                            undoDeleteButton.setEnabled(true);
                            notificationPanel.setVisible(true);

                            // Start undo timer (5 seconds)
                            undoTimer = new Timer(5000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    try {
                                        manager.removeProduct(lastDeletedProduct);
                                    } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(frame, "Error permanently deleting product from the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();
                                    }
                                    lastDeletedProduct = null;
                                    lastDeletedRow = -1;
                                    undoDeleteButton.setEnabled(false);
                                    notificationPanel.setVisible(false);
                                    undoTimer.stop();
                                }
                            });
                            undoTimer.setRepeats(false);
                            undoTimer.start();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a product to delete.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Only an admin can delete products", "Access error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Handle logout button
        exitButtonMain.addActionListener(e -> {
            frame.dispose(); // Close the current window
            SwingUtilities.invokeLater(() -> {
                new LoginDialog(); // Open login dialog again
            });
        });


        // Add the left button panel and title panel to the frame
        contentPane.add(leftButtonPanel, BorderLayout.WEST);
        contentPane.add(titleAreaPanel, BorderLayout.NORTH);

        // Center panel to hold table and search
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Panel above the table for search bar
        JPanel tableControlPanel = new JPanel(new BorderLayout());
        tableControlPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 1, 20));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        searchPanel.add(searchLabel, BorderLayout.LINE_START);
        searchTextField = new JTextField();
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchPanel.add(searchTextField, BorderLayout.CENTER);

        tableControlPanel.add(searchPanel, BorderLayout.NORTH);
        tableControlPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);

        // Title above the table
        JLabel tableTitleLabel = new JLabel("<html><u>Product Inventory</u></html>");
        tableTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tableControlPanel.add(tableTitleLabel, BorderLayout.SOUTH);

        centerPanel.add(tableControlPanel, BorderLayout.NORTH);

        // Setup product table columns
        String[] columnNames = {"Item ID", "Item Name", "Price", "Category", "Stock Levels", "Stock Status"};
        Product.tableModel.setColumnCount(columnNames.length);
        Product.tableModel.setColumnIdentifiers(columnNames);

        // Center-align the Stock Status column
        manager.itemTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        try {
            // Try rendering the data in the table
            if (manager != null && manager.getClass().getMethod("render_data").getExceptionTypes().length > 0) {
                System.out.println("render_data() might throw exceptions.");
            }
            manager.render_data();
        } catch (SQLException a) {
            System.err.println("Caught SQLException in render_data: " + a.getMessage());
            a.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error rendering data (SQLException): " + a.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Caught Exception in render_data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error rendering data (General Exception): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        manager.itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(manager.itemTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 20),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Set the preferred width of the title area
        tableControlPanel.doLayout();
        Dimension tableControlPanelSize = tableControlPanel.getPreferredSize();
        titleAreaPanel.setPreferredSize(new Dimension(tableControlPanelSize.width, titleAreaPanel.getPreferredSize().height));

        // Bottom panel for version, date, and username
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Version label (left)
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel versionSpacer = new JLabel("      ");
        JLabel versionLabelBottom = new JLabel("Version 1.0.0");
        versionLabelBottom.setFont(new Font("Arial", Font.PLAIN, 14));
        versionPanel.add(versionSpacer);
        versionPanel.add(versionLabelBottom);
        bottomPanel.add(versionPanel, BorderLayout.WEST);

        // Date and time label (center)
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dateLabel = new JLabel(getCurrentDate() + "  " + getCurrentTime());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        datePanel.add(dateLabel);
        bottomPanel.add(datePanel, BorderLayout.CENTER);

        // Logged in user (right)
        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernameLabelBottom = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Logged in as: " + username + "</html>");
        usernameLabelBottom.setFont(new Font("Arial", Font.PLAIN, 14));
        eastPanel.add(usernameLabelBottom);
        JPanel eastSpacer = new JPanel();
        eastPanel.add(eastSpacer);

        bottomPanel.add(eastPanel, BorderLayout.EAST);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Enable table sorting and filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(Product.tableModel);
        manager.itemTable.setRowSorter(sorter);

        // Add live search functionality
        JTextField finalSearchTextField = searchTextField;
        finalSearchTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterTable(finalSearchTextField.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                filterTable(finalSearchTextField.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                filterTable(finalSearchTextField.getText());
            }
        });

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Panel for Undo Delete notification
        notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
        notificationPanel.setVisible(false);
        contentPane.add(notificationPanel, BorderLayout.EAST);

        // Undo delete button
        undoDeleteButton = new JButton("Undo Delete");
        undoDeleteButton.setEnabled(false);
        undoDeleteButton.addActionListener(e -> {
            if (lastDeletedProduct != null && lastDeletedRow != -1) {
                Product.tableModel.insertRow(lastDeletedRow, new Object[]{
                        lastDeletedProduct.getId(),
                        lastDeletedProduct.getName(),
                        lastDeletedProduct.getPrice(),
                        lastDeletedProduct.getCategory(),
                        lastDeletedProduct.getQuantity()
                });
                try {
                    Data.undo_remove_Product(lastDeletedProduct);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error undoing delete in the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                lastDeletedProduct = null;
                lastDeletedRow = -1;
                undoDeleteButton.setEnabled(false);
                notificationPanel.setVisible(false);
                if (undoTimer != null && undoTimer.isRunning()) {
                    undoTimer.stop();
                }
            }
        });
        notificationPanel.add(undoDeleteButton);

        // Enable delete button only if a row is selected
        manager.itemTable.getSelectionModel().addListSelectionListener(event -> {
            deleteProductButton.setEnabled(manager.itemTable.getSelectedRow() != -1);
        });

        // Enable double-click to edit product if admin
        manager.itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (User_authenticator.getCurrent_user().getAccessLevel() == access_levels.ADMIN) {
                    if (evt.getClickCount() == 2 && manager.itemTable.getSelectedRow() != -1) {
                        int selectedRow = manager.itemTable.getSelectedRow();
                        String productName = manager.itemTable.getValueAt(selectedRow, 1).toString();
                        Product product = manager.findProduct(productName);
                        try {
                            Edit_Item.editItem(manager, product);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Error loading product");
                            e.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only an admin can edit a product", "Access error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true); // Show the frame
        startClock(); // Start clock updating every second
        return frame;
    }

    /**
     * Filter the table based on search input
     */
    private static void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) manager.itemTable.getRowSorter();
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }
}
