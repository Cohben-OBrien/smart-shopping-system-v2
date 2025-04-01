import java.awt.desktop.PreferencesEvent;

public class Product {
    String name;
    float price;
    int quantity;

    public Product(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    public void sale() {
        this.quantity--;
    }

    public float getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(float price) {
        this.price = price;
    }


}
