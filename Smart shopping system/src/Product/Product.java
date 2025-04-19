package Product;

import javax.swing.table.DefaultTableModel;

// This class represents each product in the inventory.
public class Product {


    public static DefaultTableModel tableModel = new DefaultTableModel();

    private int id;
    private String name;   // The name of the product
    private float price;   // The price of the product
    private int quantity;  // The quantity of the product in stock

    // Constructor to initialize product details
    public Product(int id, String name, float price, int quantity) {
        this.id = id;
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

    public int getId() {return id; }
    // Decrease the quantity when the product is sold
    public void sell(int quantitySold) {
        this.quantity -= quantitySold;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}






