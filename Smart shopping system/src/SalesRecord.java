package smartshop;
// Import the Product class from the same package
import smartshop.Product;  // Add this line to import the Product class

// This class represents a sales record.
public class SalesRecord {
    private Product product;  // The product that was sold
    private String date;      // The date of the sale
    private int quantity;     // The quantity of the product sold

    // Constructor to initialize sales record
    public SalesRecord(Product product, String date, int quantity) {
        this.product = product;
        this.date = date;
        this.quantity = quantity;
    }

    // Getters for the sales record details
    public Product getProduct() {
        return product;
    }

    public String getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;  // Calculate the total price of the sale
    }
}






