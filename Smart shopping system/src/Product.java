package smartshop;

/**
 * This class represents a single product in the Smart Shop system.
 */
public class Product {
    private String id;       // Unique identifier for the product
    private String name;     // The name of the product
    private double price;    // The price of the product in GBP (£)
    private int quantity;    // The quantity of the product in stock

    /**
     * Constructor to initialize all product details.
     * @param id       Product ID (e.g., "101")
     * @param name     Product name (e.g., "Boxers")
     * @param price    Product price in GBP
     * @param quantity Stock quantity
     */
    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters for accessing product details

    /**
     * @return Product ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return Quantity in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Reduce the stock quantity when a product is sold.
     * @param quantitySold The number of units sold
     */
    public void sell(int quantitySold) {
        this.quantity -= quantitySold;
    }

    /**
     * Optional: return the product details as an Object array (for tables)
     */
    public Object[] toObjectArray() {
        return new Object[]{id, name, price, quantity, ""}; // Last field is for status text
    }
}







