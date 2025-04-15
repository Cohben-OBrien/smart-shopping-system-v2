package Records;
// Import the Product class from the same package
import smartshop.Product;  // Add this line to import the Product class

// This class represents a sales record.
public class SalesRecord {
    private Product product;  // The product that was sold
    private String date;      // The date of the sale
    private double total;     // The quantity of the product sold
    private int sale_ID;

    // Constructor to initialize sales record

    public SalesRecord(Product product, String date, double total) {
        this.product = product;
        this.date = date;
        this.total = total;
    }


}






