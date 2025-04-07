package smartshop;

public class Product {
    private String name;   // Name of the product
    private float price;   // Price in pounds
    private int quantity;  // Quantity available in stock

    // Constructor
    public Product(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Method to sell a product and update quantity
    public void sell(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            System.out.println("Not enough stock!");
        }
    }

    // Method to restock the product
    public void restock(int amount) {
        this.quantity += amount;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}






