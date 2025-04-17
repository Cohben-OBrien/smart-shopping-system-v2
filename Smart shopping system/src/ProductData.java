package smartshop;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a static method to return a list of initial products
 * used in the Smart Shop system. By separating product data from the GUI,
 * the system becomes easier to maintain, expand, and reuse.
 */
public class ProductData {

    /**
     * Returns a list of pre-defined products used to populate the product table.
     *
     * Benefits of using this class instead of hardcoding in the GUI:
     * - Easier to update or add products
     * - Keeps GUI logic separate from data
     * - Can be extended later to load from database or file
     *
     * @return List<Product> containing initial products for the inventory
     */
    public static List<Product> getInitialProducts() {
        List<Product> products = new ArrayList<>();

        // Example clothing/accessory products with stock levels
        products.add(new Product("101", "Boxers", 10.00, 10));             // Low stock
        products.add(new Product("102", "Socks", 2.00, 0));                // Out of stock
        products.add(new Product("103", "T-Shirt", 15.00, 30));           // Medium stock
        products.add(new Product("104", "Blue Jeans", 35.00, 8));         // Low stock
        products.add(new Product("105", "Cotton Socks", 3.50, 75));       // High stock

        // 5 new products added to increase inventory
        products.add(new Product("106", "Hoodie", 22.00, 12));            // Medium stock
        products.add(new Product("107", "Baseball Cap", 8.00, 5));        // Low stock
        products.add(new Product("108", "Leather Belt", 18.00, 0));       // Out of stock
        products.add(new Product("109", "Casual Shorts", 16.00, 27));     // Medium stock
        products.add(new Product("110", "Sports Jacket", 45.00, 40));     // High stock

        return products;
    }
}

