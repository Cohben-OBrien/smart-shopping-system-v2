import java.util.ArrayList;
import java.util.List;


public class InventoryManager {

    private List<smartshop.Product> products;     // List of products
    private List<smartshop.SalesRecord> sales;    // List of sales made
=======
    private final List<Product> products;     // List of products
    private final List<SalesRecord> sales;    // List of sales made


    public InventoryManager() {
        products = new ArrayList<>();
        sales = new ArrayList<>();
    }

    // Add a product to the inventory
    public void addProduct(smartshop.Product product) {
        products.add(product);
    }

    // Remove a product from the inventory by name
    public void removeProduct(String name) {
        products.removeIf(product -> product.getName().equalsIgnoreCase(name));
    }

    // Find a product by its name
    public smartshop.Product findProduct(String name) {
        for (smartshop.Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;  // Not found
    }

    // Record a sale and update stock
    public boolean recordSale(String productName, int quantity, String date) {
        smartshop.Product product = findProduct(productName);
        if (product != null && product.getQuantity() >= quantity) {
            product.sell(quantity);
            sales.add(new smartshop.SalesRecord(product, date, quantity));
            return true;
        }
        return false; // Sale failed (not enough stock or product not found)
    }

    // Get list of products
    public List<smartshop.Product> getProducts() {
        return products;
    }

    // Get list of sales
    public List<smartshop.SalesRecord> getSales() {
        return sales;
    }
}
