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
    static JLabel clockLabel;

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
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
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
                clockLabel.setText(getCurrentTime());
            }
        });
        timer.start();
    }

    public static JFrame createAndShowGUI() throws SQLException {
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
                            backgroundColor = new Color(255, 255, 150);
                            statusText = "Low Stock";
                        } else if (quantity < 30) {
                            backgroundColor = new Color(255, 200, 0);
                            statusText = "Medium Stock";
                        } else {
                            backgroundColor = new Color(144, 238, 144);
                            statusText = "High Stock";
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

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("Smart Shopping System v1");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        northPanel.add(titleLabel);

        // Date Display
        dateLabel = new JLabel(getCurrentDate());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        northPanel.add(dateLabel);

        // Clock Display
        clockLabel = new JLabel(getCurrentTime());
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        northPanel.add(clockLabel);

        contentPane.add(northPanel, BorderLayout.NORTH);

        JPanel buttonSearchPanel = new JPanel(new BorderLayout(5, 5));
        buttonSearchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

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
            } catch (SQLException a) {
                System.out.println(a);
            }
        });
        buttonPanel.add(salesReportButton);

        JButton lowStockButton = new JButton("Stock Report");
        lowStockButton.setFont(new Font("Arial", Font.PLAIN, 16));
        lowStockButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Show Low Stock Report functionality."));
        buttonPanel.add(lowStockButton);

        notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        notificationPanel.setVisible(false);
        contentPane.add(notificationPanel, BorderLayout.SOUTH);

        undoDeleteButton = new JButton("Undo Delete");
        undoDeleteButton.setEnabled(false);
        undoDeleteButton.addActionListener(e -> {
            if (lastDeletedProduct != null && lastDeletedRow != -1) {
                Product.tableModel.insertRow(lastDeletedRow, new Object[]{
                        lastDeletedProduct.getId(),
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

        deleteProductButton = new JButton("Delete Product");
        deleteProductButton.setFont(new Font("Arial", Font.PLAIN, 16));
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
        buttonPanel.add(deleteProductButton);

        buttonSearchPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel searchLabel = new JLabel("Product Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        searchPanel.add(searchLabel);

        JTextField searchTextField = new JTextField(30);
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        searchPanel.add(searchTextField);

        buttonSearchPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel tableContainerPanel = new JPanel(new BorderLayout());

        // Create a panel to hold the table title with a bottom border for spacing
        JPanel tableTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel tableTitleLabel = new JLabel("Product Inventory");
        tableTitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        tableTitlePanel.add(tableTitleLabel);
        tableTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Add bottom padding

        tableContainerPanel.add(tableTitlePanel, BorderLayout.NORTH);

        String[] columnNames = {"Item ID", "Item Name", "Price", "Stock Levels", "Stock Status"};
        Product.tableModel.setColumnCount(columnNames.length);
        Product.tableModel.setColumnIdentifiers(columnNames);

        try {
            manager.render_data();
        } catch (SQLException a) {}

        manager.itemTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(manager.itemTable);
        tableContainerPanel.add(scrollPane, BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(Product.tableModel);
        manager.itemTable.setRowSorter(sorter);

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

        JPanel buttonContainerPanel = new JPanel(new BorderLayout());
        buttonContainerPanel.add(buttonSearchPanel, BorderLayout.NORTH);
        buttonContainerPanel.add(tableContainerPanel, BorderLayout.CENTER);

        contentPane.add(buttonContainerPanel, BorderLayout.CENTER);

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.addActionListener(e -> System.exit(0));
        exitPanel.add(exitButton);

        contentPane.add(exitPanel, BorderLayout.SOUTH);

        manager.itemTable.getSelectionModel().addListSelectionListener(event -> {
            deleteProductButton.setEnabled(manager.itemTable.getSelectedRow() != -1);
        });

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
}