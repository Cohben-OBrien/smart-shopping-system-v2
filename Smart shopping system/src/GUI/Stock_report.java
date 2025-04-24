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
        frame.setLayout(null);

        // Table setup
        JTable stockTable = new JTable();
        stockTable.setModel(ProductModel);
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Stock Status"};
        ProductModel.setColumnCount(columnNames.length);
        ProductModel.setColumnIdentifiers(columnNames);

        JScrollPane ProductTable = new JScrollPane(stockTable);
        ProductTable.setBounds(0, 0, 800, 500);
        frame.add(ProductTable, BorderLayout.CENTER);

        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBounds(0, 500, 800, 100);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "High", "Medium", "Low", "Out of Stock"});
        JButton applyFilter = new JButton("Apply Filter");

        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(applyFilter);
        frame.add(filterPanel);

        // Populate data
        refreshStockData();

        // Filter functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(ProductModel);
        stockTable.setRowSorter(sorter);

        applyFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            if(selected.equals("All")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected));
            }
        });

        // Color coding
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, 3);
                switch(status) {
                    case "High":
                        c.setBackground(Color.GREEN);
                        break;
                    case "Medium":
                        c.setBackground(Color.ORANGE);
                        break;
                    case "Low":
                        c.setBackground(Color.YELLOW);
                        break;
                    case "Out of Stock":
                        c.setBackground(Color.RED);
                        c.setForeground(Color.WHITE);
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        frame.setVisible(true);
    }

    private static void refreshStockData() throws SQLException {
        ProductModel.setRowCount(0);
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