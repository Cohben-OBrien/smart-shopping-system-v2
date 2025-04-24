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
    static JTextField searchTextField;
    static JPanel leftButtonPanel = new JPanel();
    static JLabel usernameLabelBottom; // Declare usernameLabelBottom here

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

            int fieldPadding = 5;
            int dialogMargin = 20;

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
            add(new JLabel());
            add(loginButton);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordStr = new String(password);

                try {
                    if (User_authenticator.User_Authemticator(username, passwordStr)) {
                        dispose();
                        System.out.println("Login successful, calling createAndShowGUI with username: " + username);
                        frame = createAndShowGUI(username);
                        System.out.println("Returned from createAndShowGUI after login");
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "Invalid username or password.", "Log In Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
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
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateLabel.setText(getCurrentDate() + "  " + getCurrentTime());
            }
        });
        timer.start();
    }

    public static JFrame createAndShowGUI(String username) throws SQLException, Exception {
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

        frame = new JFrame("IntelliShop - Smart Shopping Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create a panel to hold the spacing and the title
        JPanel titleAreaPanel = new JPanel(new BorderLayout());

        // Add an empty label with a preferred height for top spacing
        JLabel topSpacingLabel = new JLabel("");
        topSpacingLabel.setPreferredSize(new Dimension(0, 20)); // Adjust as needed
        titleAreaPanel.add(topSpacingLabel, BorderLayout.NORTH);

        // Create the title label
        JLabel titleLabel = new JLabel("IntelliShop - Smart Shopping Management");
        titleLabel.setFont(new Font("Lucida Console", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleAreaPanel.add(titleLabel, BorderLayout.CENTER);

        // Add an empty label with a preferred height for bottom padding
        JLabel bottomSpacingLabel = new JLabel("");
        bottomSpacingLabel.setPreferredSize(new Dimension(0, 10)); // Adjust this value for bottom padding
        titleAreaPanel.add(bottomSpacingLabel, BorderLayout.SOUTH);

        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Some space before the first button

        JButton productsButton = new JButton("Add Product");
        deleteProductButton = new JButton("Delete");
        JButton recordSaleButton = new JButton("Record Sale");
        JButton salesReportButton = new JButton("Sales Report");
        JButton lowStockButton = new JButton("Stock Report");
        JButton exitButton = new JButton("Log-out");

        JButton[] buttons = {productsButton, deleteProductButton, recordSaleButton, salesReportButton, lowStockButton, exitButton};
        int maxWidth = 0;
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            maxWidth = Math.max(maxWidth, button.getPreferredSize().width);
        }
        int padding = 20; // Adjust this padding value
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            button.setMaximumSize(new Dimension(maxWidth + padding, button.getPreferredSize().height));
            leftButtonPanel.add(button);
            if (i < buttons.length - 1) {
                leftButtonPanel.add(Box.createVerticalGlue()); // Glue *between* buttons
            }
        }

        leftButtonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Some space after the last button

        salesReportButton.addActionListener(e -> {
            Sales_Report report = new Sales_Report();
            try {
                report.Sales_Report();
            } catch (SQLException a) {
                System.out.println(a);
            }
        });

        lowStockButton.addActionListener(e -> {
            try {
                Stock_report.Stock_Report();
            } catch (SQLException a) {}
        });

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
        contentPane.add(titleAreaPanel, BorderLayout.NORTH); // Add the panel containing spacing and title

        JPanel centerPanel = new JPanel(new BorderLayout());

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

        JLabel tableTitleLabel = new JLabel("<html><u>Product Inventory</u></html>");
        tableTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tableTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tableControlPanel.add(tableTitleLabel, BorderLayout.SOUTH);

        centerPanel.add(tableControlPanel, BorderLayout.NORTH);

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

        // Get the preferred width of the tableControlPanel
        tableControlPanel.doLayout(); // Ensure layout is done to get accurate preferred size
        Dimension tableControlPanelSize = tableControlPanel.getPreferredSize();

        // Set the preferred width of the titleAreaPanel
        titleAreaPanel.setPreferredSize(new Dimension(tableControlPanelSize.width, titleAreaPanel.getPreferredSize().height));

        JPanel bottomPanel = new JPanel(new BorderLayout()); // Use BorderLayout

        // Panel for version number with left spacing
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // FlowLayout to arrange left to right
        JLabel versionSpacer = new JLabel("      ");
        JLabel versionLabelBottom = new JLabel("Version 1.0.0");
        versionLabelBottom.setFont(new Font("Arial", Font.PLAIN, 14));
        versionPanel.add(versionSpacer);
        versionPanel.add(versionLabelBottom);
        bottomPanel.add(versionPanel, BorderLayout.WEST);

        // Panel to hold and center the date
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dateLabel = new JLabel(getCurrentDate() + "  " + getCurrentTime());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        datePanel.add(dateLabel);
        bottomPanel.add(datePanel, BorderLayout.CENTER);

        // Panel for "Logged in as..." on the EAST with left padding
        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Use FlowLayout to arrange within EAST
        usernameLabelBottom = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Logged in as: " + username + "</html>");
        usernameLabelBottom.setFont(new Font("Arial", Font.PLAIN, 14));
        eastPanel.add(usernameLabelBottom);
        JPanel eastSpacer = new JPanel();
        eastPanel.add(eastSpacer); // Add the spacer to the east panel

        bottomPanel.add(eastPanel, BorderLayout.EAST);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);

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

        notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
        notificationPanel.setVisible(false);
        contentPane.add(notificationPanel, BorderLayout.EAST);

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
        });

        frame.setVisible(true);
        startClock();
        return frame;
    }

    private static void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) manager.itemTable.getRowSorter();
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }
}