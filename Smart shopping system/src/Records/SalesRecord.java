package Records;
// Import the Product class from the same package
import Database.Data;
import manager.InventoryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

// This class represents a sales record.
public class SalesRecord {
    private ArrayList<ProductSale> products;  // The product that was sold
    private String date;      // The date of the sale
    private double total;     // The quantity of the product sold
    private int sale_ID;

    // Constructor to initialize sales record

    public SalesRecord(int sale_ID, double total, String date) {
        this.date = date;
        this.total = total;
        this.sale_ID = sale_ID;

    }

    public SalesRecord(String date, ArrayList<ProductSale> products) throws SQLException {
        this.date = date;
        this.products = products;
        this.sale_ID = Data.get_sale_number();
    }


    private void record() {

    }

    public int get_id() {
        return this.sale_ID;
    }

    public double get_total() {
        return this.total;
    }

    public String get_date() {
        return this.date;
    }
}






