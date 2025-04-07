package smartshop;

public class SalesRecord {
    private Product product;  // The product being sold
    private String date;      // Date of the sale
    private int quantity;     // Quantity sold
    private float totalPrice; // Total price for the sale

    public SalesRecord(Product product, String date, int quantity) {
        this.product = product;
        this.date = date;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public String getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }
}







