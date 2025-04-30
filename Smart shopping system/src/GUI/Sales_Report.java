package GUI;

import Database.Data;
import Records.ProductSale;
import Records.SalesRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.sql.SQLException;
import java.util.HashMap;

public class Sales_Report {

    // This method displays the main Sales Report window
    public static void Sales_Report() throws SQLException {
        JFrame frame = new JFrame("Sales Report");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(450, 530);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // Create and configure the sales table
        JTable salesTable = new JTable();
        salesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel salesmodel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        salesTable.setModel(salesmodel);

        // Set table column headers
        String[] headings = {"Sale ID", "Sale total", "Sale date"};
        salesmodel.setColumnCount(headings.length);
        salesmodel.setColumnIdentifiers(headings);
        salesTable.setFillsViewportHeight(true);

        // Enable row sorting for the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(salesmodel);
        salesTable.setRowSorter(sorter);

        // Put the table in a scroll pane and add to the window
        JScrollPane table = new JScrollPane(salesTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        // Set the preferred width of each column
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        // Create and add the search label and input field
        JLabel search = new JLabel("Search");
        search.setBounds(0, 455, 450, 30);
        frame.getContentPane().add(search, BorderLayout.SOUTH);

        JTextField searchField = new JTextField();
        searchField.setBounds(40, 455, 400, 30);
        frame.add(searchField);

        // Filter table rows as user types in the search field
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchText = searchField.getText();
                if (searchText.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        // Fill the table with sales data from the database
        for(SalesRecord record: Data.getSalesRecords()) {
            salesmodel.addRow(new Object[]{
                    record.get_id(),
                    String.format("£%.2f", record.get_total()),
                    record.get_date()
            });
        }

        // Handle row clicks to show products from the selected sale
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                int id = Integer.parseInt(salesTable.getValueAt(row, 0).toString());

                System.out.println(id);

                try{
                    show_products(id); // Open product list window
                } catch (SQLException ex){
                    System.out.println(ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

    // This method shows the list of products for a selected sale
    public static void show_products(int sale_id) throws SQLException {

        JFrame frame = new JFrame("Sales Report");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // Create and configure the product table
        JTable ProductTable = new JTable();
        ProductTable.setCellSelectionEnabled(false);
        ProductTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel Productmodel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ProductTable.setModel(Productmodel);

        // Set column headers
        String[] headings = {"Product ID", "Product Name", "Product Price", "Quantity Sold", "Total cost"};
        Productmodel.setColumnCount(headings.length);
        Productmodel.setColumnIdentifiers(headings);

        // Set width for each column
        for(int i = 0; i < headings.length; i++) {
            ProductTable.getColumnModel().getColumn(i).setPreferredWidth(180);
        }

        // Add table to scroll pane and then to frame
        JScrollPane table = new JScrollPane(ProductTable);
        table.setBounds(0, 0, 910, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        frame.setVisible(true);

        // Load product data for this sale into the table
        for(ProductSale productSale: Data.getProductSales(sale_id)) {
            double total_cost = productSale.getQuantity() * productSale.getProduct().getPrice();
            System.out.println(total_cost);
            Productmodel.addRow(new Object[]{
                    productSale.getProduct().getId(),
                    productSale.getProduct().getName().replace("_", " "),
                    String.format("£%.2f", productSale.getProduct().getPrice()),
                    productSale.getQuantity(),
                    String.format("£%.2f", productSale.getProduct().getPrice() * productSale.getQuantity())
            });
        }
    }
}
