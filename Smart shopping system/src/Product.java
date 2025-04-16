package smartshop;

public class Product {
    // Fields to store product details
    private String name;    // Name of the product
    private float price;    // Price per unit
    private int quantity;   // Stock quantity

    /**
     * Constructor to create a new product
     * @param name Name of the product
     * @param price Price per unit
     * @param quantity Available quantity
     */
    public Product(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Get the product's name
     * @return Name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Get the price per unit
     * @return Price as a float
     */
    public float getPrice() {
        return price;
    }

    /**
     * Get the quantity in stock
     * @return Quantity as an int
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set a new quantity (can be used after a sale or stock update)
     * @param quantity New stock quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Reduce the quantity when a sale is made
     * @param amount The number of units sold
     */
    public void sell(int amount) {
        this.quantity -= amount;
    }

    /**
     * Optional: Print-friendly format for the product
     * @return Product details as a String
     */
    @Override
    public String toString() {
        return name + " - £" + price + " (" + quantity + " in stock)";
    }
}








