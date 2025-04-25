package GUI;

import Database.Data;
import Product.Product;
import manager.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class Stock_report {
    static DefaultTableModel ProductModel = new DefaultTableModel();
    static ArrayList<Product> products = new ArrayList<>();

    public static void Stock_Report() throws SQLException {
        JFrame frame = new JFrame("Stock Report");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Table setup
        JTable stockTable = new JTable();
        stockTable.setModel(ProductModel);
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Stock Status"};
        ProductModel.setColumnIdentifiers(columnNames);

        // Custom renderer for status column
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                c.setForeground(Color.BLACK); // All text remains black
                
                if (column == 3) { // Only color the status column
                    String status = (String) value;
                    switch(status) {
                        case "High":
                            c.setBackground(new Color(144, 238, 144)); // Light green
                            break;
                        case "Medium":
                            c.setBackground(new Color(255, 200, 0)); // Orange
                            break;
                        case "Low":
                            c.setBackground(new Color(255, 255, 150)); // Light yellow
                            break;
                        case "Out of Stock":
                            c.setBackground(new Color(255, 99, 71)); // Red
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Filter components
        JPanel filterPanel = new JPanel();
        JComboBox<String> statusFilter = new JComboBox<>(
            new String[]{"All", "High", "Medium", "Low", "Out of Stock"}
        );
        JButton applyFilter = new JButton("Apply Filter");
        
        // Table sorting/filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(ProductModel);
        stockTable.setRowSorter(sorter);

        // Filter action
        applyFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            if(selected.equals("All")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected));
            }
        });

        // Layout components
        JScrollPane scrollPane = new JScrollPane(stockTable);
        filterPanel.add(new JLabel("Filter Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(applyFilter);

        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshStockData();
        frame.setVisible(true);
    }

    private static void refreshStockData() throws SQLException {
        ProductModel.setRowCount(0); // Clear existing data
        products = Data.getProducts();
        
        for(Product product : products) {
            String status = getStockStatus(product.getQuantity());
            ProductModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getQuantity(),
                status
            });
        }
    }

    private static String getStockStatus(int quantity) {
        if(quantity == 0) return "Out of Stock";
        if(quantity < 10) return "Low";
        if(quantity < 30) return "Medium";
        return "High";
    }
}