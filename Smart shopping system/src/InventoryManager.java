package smartshop;

import Database.Data;
import Records.SalesRecord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// This class manages all products and sales in the system.
public class InventoryManager {
    private static List<smartshop.Product> products; // List to store all the products in inventory
    private static List<SalesRecord> sales; // List to store all sales made


    // Constructor: initializes the lists
    public InventoryManager() {
        products = new ArrayList<>(); // Initialize the products list
        sales = new ArrayList<SalesRecord>(); // Initialize the sales list
    }

    // Add a product to the inventory
    public void addProduct(smartshop.Product product) throws SQLException{
        products.add(product); // Add product to the list of products
        Data.addProduct(product);

    }



    // Record a sale (decrease stock and add to sales record)
    public static void recordSale(Product product, String date, double sale) {
        sales.add(new SalesRecord(product, date, sale));
    }


    // Find a product by its name
    public static smartshop.Product findProduct(String productName) {
        for (smartshop.Product p : products) {
            if (p.getName().equalsIgnoreCase(productName)) {
                return p;  // Return the found product
            }
        }
        return null;  // If not found, return null
    }

    // Get the list of all products
    public static List<smartshop.Product> getProducts() {
        return products;
    }

    // Get the list of all sales
    public List<SalesRecord> getSales() {
        return sales;
    }

    // Get a list of products that are low in stock (less than 5 units)
    public List<smartshop.Product> getLowStockProducts() {
        List<smartshop.Product> lowStock = new ArrayList<>();
        for (smartshop.Product p : products) {
            if (p.getQuantity() < 5) {
                lowStock.add(p);  // Add products with low stock to the list
            }
        }
        return lowStock;
    }

    public void loadInventory() throws SQLException {
        products = Data.getProducts();
    }

    public static int next_id() {
        return products.getLast().getId() + 1;
    }
}
