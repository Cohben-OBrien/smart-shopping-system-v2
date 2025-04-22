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
import java.sql.SQLException;
import java.util.HashMap;

public class Sales_Report {

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

       String[] headings = {"Sale ID", "Sale total", "Sale date"};
       salesmodel.setColumnCount(headings.length);
       salesmodel.setColumnIdentifiers(headings);

       salesTable.setFillsViewportHeight(true);


        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(salesmodel);
        salesTable.setRowSorter(sorter);

       JScrollPane table = new JScrollPane(salesTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150);



        for(SalesRecord record: Data.getSalesRecords()) {
            salesmodel.addRow(new Object[]{record.get_id(), String.format("£%.2f", record.get_total()), record.get_date()});
        }


        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                int id = Integer.parseInt(salesTable.getValueAt(row, 0).toString());

                System.out.println(id);

                try{
                    show_products(id);
                } catch (SQLException ex){
                    System.out.println(ex.getMessage());
                }


            }
        });


       frame.setVisible(true);


    }

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

        String[] headings = {"Product ID", "Product Name", "Product Price", "Quantity Sold", "Total cost"};
        Productmodel.setColumnCount(headings.length);
        Productmodel.setColumnIdentifiers(headings);

        for(int i = 0; i < headings.length; i++) {
            ProductTable.getColumnModel().getColumn(i).setPreferredWidth(90);
        }




        JScrollPane table = new JScrollPane(ProductTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);

        frame.setVisible(true);

        for(ProductSale productSale: Data.getProductSales(sale_id)) {
            double total_cost = productSale.getQuantity() * productSale.getProduct().getPrice();
            System.out.println(total_cost);
            Productmodel.addRow(new Object[]{productSale.getProduct().getId(), productSale.getProduct().getName(), String.format("£%.2f", productSale.getProduct().getPrice()), productSale.getQuantity(), String.format("£%.2f", productSale.getProduct().getPrice() * productSale.getQuantity())});
        }
    }

}
