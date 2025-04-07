import java.util.ArrayList;
import java.util.List;


public class InventoryManager {
    private final List<Product> products;     // List of products
    private final List<SalesRecord> sales;    // List of sales made

    public InventoryManager() {
        products = new ArrayList<>();
        sales = new ArrayList<>();
    }

    // Add a product to the inventory
    public void addProduct(Product product) {
        products.add(product);
    }

    // Remove a product from the inventory by name
    public void removeProduct(String name) {
        products.removeIf(product -> product.getName().equalsIgnoreCase(name));
    }

    // Find a product by its name
    public Product findProduct(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;  // Not found
    }

    // Record a sale and update stock
    public boolean recordSale(String productName, int quantity, String date) {
        Product product = findProduct(productName);
        if (product != null && product.getQuantity() >= quantity) {
            product.sell(quantity);
            sales.add(new SalesRecord(product, date, quantity));
            return true;
        }
        return false; // Sale failed (not enough stock or product not found)
    }

    // Get list of products
    public List<Product> getProducts() {
        return products;
    }

    // Get list of sales
    public List<SalesRecord> getSales() {
        return sales;
    }
}
