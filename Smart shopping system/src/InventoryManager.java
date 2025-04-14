package smartshop;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private List<Product> products;  // List to store all the products in inventory
    private List<SalesRecord> sales; // List to store all sales made

    // Constructor: Initializes the product and sales lists
    public InventoryManager() {
        products = new ArrayList<>();
        sales = new ArrayList<>();
    }

    // Method to add a product to the inventory
    public void addProduct(Product product) {
        products.add(product);  // Add the product to the inventory
    }

    // Method to record a sale (reduce stock and add to sales record)
    public boolean recordSale(String productName, int quantitySold, String date) {
        Product product = findProduct(productName);  // Find the product by name
        if (product != null && product.getQuantity() >= quantitySold) {
            product.sell(quantitySold);  // Reduce the stock of the product
            sales.add(new SalesRecord(product, date, quantitySold));  // Record the sale
            return true;  // Sale was successful
        }
        return false;  // Sale failed (either product not found or insufficient stock)
    }

    // Method to find a product by its name
    public Product findProduct(String productName) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(productName)) {
                return p;  // Return the product if found
            }
        }
        return null;  // Return null if product is not found
    }

    // Get a list of all products
    public List<Product> getProducts() {
        return products;  // Return the list of products
    }

    // Get a list of all sales made
    public List<SalesRecord> getSales() {
        return sales;  // Return the list of sales records
    }

    // Get a list of products that are low in stock (less than 5 units)
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getQuantity() < 5) {
                lowStock.add(p);  // Add products with low stock to the list
            }
        }
        return lowStock;  // Return the list of low stock products
    }
}

