package smartshop;

// This class represents each product in the inventory.
public class Product {
    private String name;   // The name of the product
    private float price;   // The price of the product
    private int quantity;  // The quantity of the product in stock

    // Constructor to initialize product details
    public Product(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters for the product details
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Decrease the quantity when the product is sold
    public void sell(int quantitySold) {
        this.quantity -= quantitySold;
    }
}






