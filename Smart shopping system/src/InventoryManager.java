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

    /**
     * Add a product to the inventory list
     * @param product The product to be added
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Record a sale and update product stock
     * @param productName Name of product sold
     * @param quantitySold Quantity to sell
     * @param date Date of the sale
     * @return true if sale is successful, false otherwise
     */
    public boolean recordSale(String productName, int quantitySold, String date) {
        Product product = findProduct(productName);
        if (product != null && product.getQuantity() >= quantitySold) {
            product.sell(quantitySold);  // Decrease stock
            sales.add(new SalesRecord(product, date, quantitySold));  // Save the sale
            return true;
        }
        return false;  // Sale failed: not found or not enough stock
    }

    /**
     * Find a product by name (case-insensitive)
     * @param productName Name to search
     * @return Product object if found, otherwise null
     */
    public Product findProduct(String productName) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(productName)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Get the full list of products
     * @return List of all products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Get the full list of sales
     * @return List of all sales records
     */
    public List<SalesRecord> getSales() {
        return sales;
    }

    /**
     * Get products that are low in stock (2 units or less)
     * This matches the GUI logic that highlights red rows for critical stock
     * @return List of products with stock ≤ 2
     */
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getQuantity() <= 2) {  // Match with GUI red text logic
                lowStock.add(p);
            }
        }
        return lowStock;
    }
}


