package Records;
// Import the Product class from the same package
import manager.InventoryManager;

import java.util.ArrayList;

// This class represents a sales record.
public class SalesRecord {
    private ArrayList<ProductSale> products;  // The product that was sold
    private String date;      // The date of the sale
    private double total;     // The quantity of the product sold
    private int sale_ID;

    // Constructor to initialize sales record

    public SalesRecord(String date, ArrayList<ProductSale> products) {
        this.date = date;
        this.products = products;
        this.sale_ID = InventoryManager.sales_next_id();
    }

    private void record() {

    }

    public int get_id() {
        return this.sale_ID;
    }


    public String get_date() {
        return this.date;
    }
}






