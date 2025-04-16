package smartshop;

public class SalesRecord {
    private Product product;  // The product that was sold
    private String date;      // The date of the sale
    private int quantity;     // The quantity of the product sold

    /**
     * Constructor: Initializes the sales record with product, date, and quantity sold
     * @param product The product being sold
     * @param date The date the sale occurred (format: dd/mm/yyyy)
     * @param quantity The number of items sold
     */
    public SalesRecord(Product product, String date, int quantity) {
        this.product = product;      // Store the sold product
        this.date = date;            // Store the date of the sale
        this.quantity = quantity;    // Store how many units were sold
    }

    /**
     * Returns the product involved in the sale
     * @return Product object
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the date the sale was made
     * @return String representing the sale date
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns how many units of the product were sold
     * @return int value of quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Calculates the total price of the sale
     * Total = product price * quantity sold
     * @return float representing total sale value
     */
    public float getTotalPrice() {
        return product.getPrice() * quantity;
    }

    /**
     * Optional: A readable string version of the sales record
     * This is useful for printing in logs, consoles, or text-based reports
     */
    @Override
    public String toString() {
        return date + " - " + product.getName() + " - " + quantity + " units - £" + getTotalPrice();
    }
}








