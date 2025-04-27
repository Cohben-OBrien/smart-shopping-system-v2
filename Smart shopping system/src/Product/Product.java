package Product;

import javax.swing.table.DefaultTableModel;
import Product.Product_Category;


// This class represents each product in the inventory.
public class Product {


    public static DefaultTableModel tableModel = new DefaultTableModel();

    private int id;
    private String name;   // The name of the product
    private float price;   // The price of the product
    private int quantity;  // The quantity of the product in stock
    private Product_Category category;
    private boolean selling;

    // Constructor to initialize product details
    public Product(int id, String name, float price, int quantity, Product_Category category, boolean selling) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.selling = selling;
    }

    public boolean isSelling() {
        return selling;
    }

    public void setCategory(Product_Category category) {
        this.category = category;
    }
    public void setQuantity(int quantity){
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
    public Product_Category getCategory() {return category;}

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
}






