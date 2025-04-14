package smartshop;

public class SalesRecord {
    private Product product;  // The product that was sold
    private String date;      // The date of the sale
    private int quantity;     // The quantity of the product sold

    // Constructor: Initializes the sales record with product, date, and quantity sold
    public SalesRecord(Product product, String date, int quantity) {
        this.product = product;  // Set the product sold
        this.date = date;        // Set the sale date
        this.quantity = quantity;  // Set the quantity sold
    }

    // Getter methods to access the sales record details
    public Product getProduct() {
        return product;  // Returns the product that was sold
    }

    public String getDate() {
        return date;  // Returns the date of the sale
    }

    public int getQuantity() {
        return quantity;  // Returns the quantity of the product sold
    }

    // This method calculates the total price of the sale (product price * quantity sold)
    public float getTotalPrice() {
        return product.getPrice() * quantity;  // Multiply the product price by the quantity sold
    }
}







