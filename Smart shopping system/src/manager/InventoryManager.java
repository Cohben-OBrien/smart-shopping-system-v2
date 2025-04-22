package manager;

import Database.Data;
import Product.Product;
import Records.ProductSale;
import Records.SalesRecord;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// This class manages the inventory and sales records
public class InventoryManager extends Main {

    private static List<Product> products; // List to store all products
    private static List<SalesRecord> sales; // List to store all sales

    public static JTable itemTable;

    // Reloads product data into the table UI
    public static void render_data() throws SQLException {
        Product.tableModel.setRowCount(0);
        loadInventory();
        for (Product product : getProducts()) {
            System.out.println(product.getName() + product.getQuantity());
            Product.tableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity()
            });
        }

        itemTable.repaint();
    }

    // Constructor - initializes the product and sales lists
    public InventoryManager() {
        products = new ArrayList<>();
        sales = new ArrayList<SalesRecord>();
    }

    // Return the list of products (non-static access)
    public List<Product> GetProducts() {
        return products;
    }

    // Add a new product to the system and database
    public void addProduct(Product product) throws SQLException {
        products.add(product);
        Data.addProduct(product);
        Product.tableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        });
    }

    // Record a new sale: updates database and UI
    public static void recordSale(ArrayList<ProductSale> productSales, String date) throws SQLException {
        sales.add(new SalesRecord(date, productSales));
        System.out.println("ID: " + sales.getLast().get_id());

        Data.Add_Sale(productSales, sales.getLast());
        render_data(); // Update inventory after the sale
    }

    // Find a product by its name (case-insensitive)
    public static Product findProduct(String productName) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(productName)) {
                return p;
            }
        }
        return null;
    }

    // Get the list of all products (static access)
    public static List<Product> getProducts() {
        return products;
    }

    // Get the list of all recorded sales
    public static List<SalesRecord> getSales() {
        return sales;
    }

    // Return a list of products that are low on stock (<5 units)
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getQuantity() < 5) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    // Load products from the database and sort by ID
    public static void loadInventory() throws SQLException {
        products = Data.getProducts();
        products.sort(Comparator.comparingInt(Product::getId));
    }

    // Get the next available product ID
    public static int product_next_id() {
        try {
            return products.getLast().getId() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // Get the next available sale ID
    public static int sales_next_id() {
        try {
            return sales.getLast().get_id() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // Update a product's name, price, and quantity
    public static void Update_Product(Product product, String Name, float Price, int Quantity) throws SQLException {
        String Previous_name = product.getName();

        // Update values in memory
        products.get(products.indexOf(product)).setName(Name);
        products.get(products.indexOf(product)).setPrice(Price);
        products.get(products.indexOf(product)).setQuantity(Quantity);

        // Update values in database and refresh UI
        Data.update_Product(products.get(products.indexOf(product)), Previous_name);
        render_data();
    }

    // Remove a product from both the inventory and the database
    public static void removeProduct(Product product) throws SQLException {
        Data.remove_Product(product.getId());
        products.remove(product);
    }
}



