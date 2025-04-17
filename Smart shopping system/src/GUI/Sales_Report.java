package GUI;

import Database.Data;
import Records.SalesRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

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


       JScrollPane table = new JScrollPane(salesTable);
        table.setBounds(0, 0, 450, 450);
        frame.getContentPane().add(table, BorderLayout.CENTER);
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150);


        for(SalesRecord record: Data.getSalesRecords()) {
            salesmodel.addRow(new Object[]{record.get_id(), record.get_total(), record.get_date()});
        }



       frame.setVisible(true);


    }

}
