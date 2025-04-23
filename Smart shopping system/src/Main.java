package GUI;

import Database.Data;
import Product.Product;
import User.User_authenticator;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class Main extends JFrame {

    static InventoryManager manager = new InventoryManager();
    static JFrame frame;
    static JButton deleteProductButton;
    static JButton undoDeleteButton;
    static Product lastDeletedProduct = null;
    static int lastDeletedRow = -1;
    static Timer undoTimer;
    static JPanel notificationPanel;
    static JLabel dateLabel;
    static JTextField searchTextField; // Declare searchTextField at the class level
    static JPanel leftButtonPanel = new JPanel(); // Declare leftButtonPanel here

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginDialog();
        });
    }

    static class LoginDialog extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;

        public LoginDialog() {
            setTitle("Log In");
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setModal(true);
            setLayout(new GridLayout(3, 2, 15, 15));
            setPreferredSize(new Dimension(400, 200));
            setLocationRelativeTo(null);

            int fieldPadding = 5; // Padding around the text fields and button
            int dialogMargin = 20; // Padding around the entire dialog

            // Create an EmptyBorder for the content pane
            ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(
                    dialogMargin,
                    dialogMargin,
                    dialogMargin,
                    dialogMargin
            ));

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            usernameField = new JTextField(15);
            usernameField.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));
            passwordField = new JPasswordField(15);
            passwordField.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));

            loginButton = new JButton("Log In");
            loginButton.setBorder(BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding));

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
                        dispose();
                        try {
                            frame = createAndShowGUI();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (Exception ex) { // Catch both SQLException and IOException
                    JOptionPane.showMessageDialog(LoginDialog.this, "An error occurred during login: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Login error: " + ex.getMessage());
                    ex.printStackTrace();
                    passwordField.setText("");
                }
            });

            pack();
            setVisible(true);
        }
    }

    public static void Add_product_to_table(Product product) {
        Product.tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice(), product.getQuantity()});
    }

    private static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return currentDate.format(formatter);
    }

    private static String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    private static void startClock() {
        Timer timer = new Timer(1000, new ActionListener() { // Update every 1000 milliseconds (1 second)
            @Override
            public void actionPerformed(ActionEvent e) {

                dateLabel.setText(getCurrentDate() + "  " + getCurrentTime()); // Display date and time
            }
        });
        timer.start();
    }

    public static JFrame createAndShowGUI() throws SQLException, Exception {
        manager.loadInventory();

        manager.itemTable = new JTable(Product.tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int stockColumn = 3;
                int statusColumn = 4;
                int priceColumn = 2;

                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);

                if (column == priceColumn) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Number) {
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
                        setValueAt(currencyFormat.format(value), row, column);
                    }
                }

                if (column == statusColumn) {
                    Object stockValue = getValueAt(row, stockColumn);
                    try {
                        int quantity = Integer.parseInt(stockValue.toString());
                        String statusText;
                        Color backgroundColor;

                        if (quantity == 0) {
                            backgroundColor = new Color(255, 99, 71);
                            statusText = "Out of Stock";
                        } else if (quantity < 10) {
                            backgroundColor = new Color(255, 200, 0);
                            statusText = "Low";
                        } else if (quantity < 30) {
                            backgroundColor = new Color(255, 255, 150);
                            statusText = "Medium";
                        } else {
                            backgroundColor = new Color(144, 238, 144);
                            statusText = "High";
                        }
                        c.setBackground(backgroundColor);
                        setValueAt(statusText, row, column);

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
        manager.itemTable.setDefaultEditor(Object.class, null);
        manager.itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        manager.itemTable.setRowSelectionAllowed(true);

        frame = new JFrame("Smart Shopping System v1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 700);
        frame.setLocationRelativeTo(null);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create the title label
        JLabel titleLabel = new JLabel("InteliShop - Smart Shopping Management");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text within the label

        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load the image
        java.net.URL imageUrl = Main.class.getResource("/resources/shopping_cart_icon.png");
        System.out.println("Image URL: " + imageUrl);
        ImageIcon originalIcon = new ImageIcon(imageUrl);

        // Get the Image from the ImageIcon
        Image originalImage = originalIcon.getImage();

        // Define the desired width and height for the scaled image
        int scaledWidth = 75;
        int scaledHeight = 75;
        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftButtonPanel.add(imageLabel);
        leftButtonPanel.add(Box.createVerticalStrut(5));

        // Add a rigid area here to create space before the buttons
        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Increased space to 50 pixels

        JButton productsButton = new JButton("Add Product");
        deleteProductButton = new JButton("Delete");
        JButton recordSaleButton = new JButton("Record Sale");
        JButton salesReportButton = new JButton("Sales");
        JButton lowStockButton = new JButton("Stock");
        JButton exitButton = new JButton("Log-out");

        JButton[] buttons = {productsButton, deleteProductButton, recordSaleButton, salesReportButton, lowStockButton, exitButton};
        int maxWidth = 0;
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 16));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            maxWidth = Math.max(maxWidth, button.getPreferredSize().width);
        }
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            button.setMaximumSize(new Dimension(maxWidth, button.getPreferredSize().height));
            leftButtonPanel.add(button);
            if (i < buttons.length - 1) {
                leftButtonPanel.add(Box.createVerticalGlue());
            }
        }
        leftButtonPanel.add(Box.createVerticalGlue());

        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the text
        leftButtonPanel.add(versionLabel);

        salesReportButton.addActionListener(e -> {
            Sales_Report report = new Sales_Report();
            try {
                report.Sales_Report();
            } catch (SQLException a) {
                System.out.println(a);
            }
        });

        lowStockButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Low Stock Report functionality."));

        productsButton.addActionListener(e -> {
            New_Item item = new New_Item();
            try {
                item.newItem(manager);
            } catch (SQLException a) {}
        });

        recordSaleButton.addActionListener(e -> {
            Add_Sale.Add_Sale(manager);
        });

        deleteProductButton.setEnabled(false);
        deleteProductButton.addActionListener(e -> {
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
        });

        exitButton.addActionListener(e -> System.exit(0));

        contentPane.add(leftButtonPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());

        // Create the table control panel first
        JPanel tableControlPanel = new JPanel(new BorderLayout()); // Use BorderLayout for tableControlPanel
        tableControlPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 1, 20)); // Increased horizontal padding

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0)); // Use BorderLayout for searchPanel
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        searchPanel.add(searchLabel, BorderLayout.LINE_START);
        searchTextField = new JTextField(); // Let the TextField expand
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchPanel.add(searchTextField, BorderLayout.CENTER); // TextField in the CENTER

        tableControlPanel.add(searchPanel, BorderLayout.NORTH); // Add searchPanel to the NORTH

        tableControlPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);

        JLabel tableTitleLabel = new JLabel("<html><u>Product Inventory</u></html>");
        tableTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tableControlPanel.add(tableTitleLabel, BorderLayout.SOUTH);

        // Create a new panel to hold the main title and table controls
        JPanel titleAndControlPanel = new JPanel(new BorderLayout());
        titleAndControlPanel.add(titleLabel, BorderLayout.NORTH);
        titleAndControlPanel.add(tableControlPanel, BorderLayout.SOUTH); // Add the tableControlPanel to the SOUTH

        // Add the title and control panel to the NORTH of the centerPanel
        centerPanel.add(titleAndControlPanel, BorderLayout.NORTH);

        String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels", "Stock Status"};
        Product.tableModel.setColumnCount(columnNames.length);
        Product.tableModel.setColumnIdentifiers(columnNames);

        try {
            manager.render_data();
        } catch (SQLException a) {}

        manager.itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(manager.itemTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 20),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout.RIGHT
        dateLabel = new JLabel(getCurrentDate() + "  " + getCurrentTime()); // Display date and time initially
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateTimePanel.add(dateLabel);
        // clockLabel is no longer added separately to dateTimePanel

        dateTimePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(dateTimePanel, BorderLayout.SOUTH); // Added to centerPanel SOUTH

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(Product.tableModel);
        manager.itemTable.setRowSorter(sorter);

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

        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
        notificationPanel.setVisible(false);
        contentPane.add(notificationPanel, BorderLayout.SOUTH);

        undoDeleteButton = new JButton("Undo Delete");
        undoDeleteButton.setEnabled(false);
        undoDeleteButton.addActionListener(e -> {
            if (lastDeletedProduct != null && lastDeletedRow != -1) {
                Product.tableModel.insertRow(lastDeletedRow, new Object[]{
                        lastDeletedProduct.getId(),       // Add ID if required
                        lastDeletedProduct.getName(),
                        lastDeletedProduct.getPrice(),
                        lastDeletedProduct.getQuantity()
                });
                try {
                    manager.addProduct(lastDeletedProduct);
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

        manager.itemTable.getSelectionModel().addListSelectionListener(event -> {
            deleteProductButton.setEnabled(manager.itemTable.getSelectedRow() != -1);
        });

        manager.itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
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
            }
        }); // Corrected: Added the missing semicolon here

        frame.setVisible(true);
        startClock(); // Call startClock() after making the frame visible
        return frame;
    }

    private static void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) manager.itemTable.getRowSorter();
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // "(?i)" for case-insensitive search
        }
    }
}