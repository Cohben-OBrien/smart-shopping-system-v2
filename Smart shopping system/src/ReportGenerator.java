package smartshop;

import javax.swing.*;
import java.util.List;

public class ReportGenerator {

    // Show a simple sales report in JTextArea
    public static void showSalesReport(List<smartshop.SalesRecord> sales, JTextArea reportArea) {
        reportArea.setText("---- SALES REPORT ----\n");
        for (smartshop.SalesRecord record : sales) {
            reportArea.append(record.getDate() + " - " +
                    record.getProduct().getName() + " - " +
                    record.getQuantity() + " units - £" +
                    String.format("%.2f", record.getTotalPrice()) + "\n");
        }
        reportArea.append("-----------------------\n");
    }

    // Show low stock products
    public static void showLowStockReport(List<smartshop.Product> products, int limit, JTextArea reportArea) {
        reportArea.setText("---- LOW STOCK REPORT ----\n");
        for (smartshop.Product p : products) {
            if (p.getQuantity() <= limit) {
                reportArea.append(p.getName() + " → only " + p.getQuantity() + " left.\n");
            }
        }
        reportArea.append("---------------------------\n");
    }
}











