package smartshop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create the inventory manager (handles all logic)
        InventoryManager manager = new InventoryManager();

        // Create the main application window
        JFrame frame = new JFrame("Smart Shop System");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel for text fields and buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Text fields for user input
        JTextField productNameField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField quantityField = new JTextField(10);

        // Buttons for actions
        JButton addProductButton = new JButton("Add Product");
        JButton recordSaleButton = new JButton("Record Sale");
        JButton showSalesButton = new JButton("Show Sales Report");
        JButton lowStockButton = new JButton("Show Low Stock Report");

        // Add input fields and buttons to the panel
        buttonPanel.add(new JLabel("Product Name:"));
        buttonPanel.add(productNameField);
        buttonPanel.add(new JLabel("Price:"));
        buttonPanel.add(priceField);
        buttonPanel.add(new JLabel("Quantity:"));
        buttonPanel.add(quantityField);
        buttonPanel.add(addProductButton);
        buttonPanel.add(recordSaleButton);
        buttonPanel.add(showSalesButton);
        buttonPanel.add(lowStockButton);

        // Add the panel to the top of the window
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Product", "Price", "Quantity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable productTable = new JTable(tableModel);

        // Style the table for better appearance
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        productTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        // Highlight rows with low stock (≤ 2) in red
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    int quantity = (int) table.getModel().getValueAt(row, 2);
                    if (quantity <= 2) {
                        c.setForeground(Color.RED); // Low stock: red text
                    } else {
                        c.setForeground(Color.BLACK); // Normal stock: black text
                    }
                } catch (Exception ex) {
                    c.setForeground(Color.BLACK); // Default color in case of error
                }
                return c;
            }
        });

        // Add the table to a scroll pane and into the main window
        JScrollPane scrollPane = new JScrollPane(productTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // When "Add Product" button is clicked
        addProductButton.addActionListener(e -> {
            try {
                String name = productNameField.getText().trim();
                float price = Float.parseFloat(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // Check if product name is empty
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Product name cannot be empty.");
                    return;
                }

                // Add product to manager and table
                Product newProduct = new Product(name, price, quantity);
                manager.addProduct(newProduct);
                tableModel.addRow(new Object[]{name, price, quantity});

                // Clear input fields after adding
                productNameField.setText("");
                priceField.setText("");
                quantityField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers for price and quantity.");
            }
        });

        // When "Record Sale" button is clicked
        recordSaleButton.addActionListener(e -> {
            try {
                String saleName = JOptionPane.showInputDialog("Enter product name:");
                int saleQty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to sell:"));
                String date = JOptionPane.showInputDialog("Enter date (dd/mm/yyyy):");

                boolean success = manager.recordSale(saleName, saleQty, date);

                if (success) {
                    JOptionPane.showMessageDialog(frame, "Sale recorded!");

                    // Update the quantity in the table
                    Product updatedProduct = manager.findProduct(saleName);
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(saleName)) {
                            tableModel.setValueAt(updatedProduct.getQuantity(), i, 2); // Update quantity
                            break;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Sale failed (check product name or stock).");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid quantity entered.");
            }
        });

        // When "Show Sales Report" button is clicked
        showSalesButton.addActionListener(e -> {
            String report = "---- SALES REPORT ----\n";
            List<SalesRecord> sales = manager.getSales();
            for (SalesRecord record : sales) {
                report += record.getDate() + " - " + record.getProduct().getName() + " - " +
                        record.getQuantity() + " units - £" + record.getTotalPrice() + "\n";
            }
            JOptionPane.showMessageDialog(frame, report);
        });

        // When "Show Low Stock Report" button is clicked
        lowStockButton.addActionListener(e -> {
            String report = "---- LOW STOCK REPORT ----\n";
            List<Product> lowStockProducts = manager.getLowStockProducts();
            for (Product product : lowStockProducts) {
                report += product.getName() + " → only " + product.getQuantity() + " left.\n";
            }
            JOptionPane.showMessageDialog(frame, report);
        });

        // Show the full application window
        frame.setVisible(true);
    }
}
























