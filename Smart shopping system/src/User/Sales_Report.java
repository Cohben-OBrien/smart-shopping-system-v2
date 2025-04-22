package User;

import Database.Data;
import Records.ProductSale;
import Records.SalesRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Sales_Report {

    // Main method to display a table with all sales records
    public static void Sales_Report() throws SQLException {
        JFrame frame = new JFrame("Sales Report");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(450, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JTable salesTable = new JTable();
        salesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel salesmodel = new DefaultTableModel();
        salesTable.setModel(salesmodel);

        // Define column headers for the sales table
        String[] headings = {"Sale ID", "Sale total", "Sale date"};
        salesmodel.setColumnCount(headings.length);
        salesmodel.setColumnIdentifiers(headings);

        salesTable.setFillsViewportHeight(true);

        // Enable sorting of table rows
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(salesmodel);
        salesTable.setRowSorter(sorter);

        JScrollPane table = new JScrollPane(salesTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        // Add sales data to the table
        for (SalesRecord record : Data.getSalesRecords()) {
            salesmodel.addRow(new Object[]{
                    record.get_id(),
                    String.format("£%.2f", record.get_total()),
                    record.get_date()
            });
        }

        // Detect when a row is clicked to show the products in that sale
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                int id = Integer.parseInt(salesTable.getValueAt(row, 0).toString());

                System.out.println(id);

                try {
                    show_products(id); // Show products for the selected sale
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

    // Opens a new window showing products in a specific sale
    public static void show_products(int sale_id) throws SQLException {
        JFrame frame = new JFrame("Sales Report");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(465, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JTable ProductTable = new JTable();
        ProductTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel Productmodel = new DefaultTableModel();
        ProductTable.setModel(Productmodel);

        // Set column headers for product table
        String[] headings = {"Product ID", "Product Name", "Product Price", "Quantity Sold", "Total cost"};
        Productmodel.setColumnCount(headings.length);
        Productmodel.setColumnIdentifiers(headings);

        // Set preferred column widths
        for (int i = 0; i < headings.length; i++) {
            ProductTable.getColumnModel().getColumn(i).setPreferredWidth(90);
        }

        JScrollPane table = new JScrollPane(ProductTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        frame.setVisible(true);

        // Populate the product table with data from the selected sale
        for (ProductSale productSale : Data.getProductSales(sale_id)) {
            double total_cost = productSale.getQuantity() * productSale.getProduct().getPrice();
            System.out.println(total_cost);
            Productmodel.addRow(new Object[]{
                    productSale.getProduct().getId(),
                    productSale.getProduct().getName(),
                    String.format("£%.2f", productSale.getProduct().getPrice()),
                    productSale.getQuantity(),
                    String.format("£%.2f", total_cost)
            });
        }
    }
}
