package manager;

import Database.Data;
import Product.Product;
import Product.Product_Category;
import Product.Categories;
import Records.ProductSale;
import Records.SalesRecord;
import com.sun.tools.javac.Main;
import jdk.jfr.Category;

import javax.swing.*;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// This class manages all products and sales in the system.
public class InventoryManager extends Main {
    private static List<Product> products; // List to store all the products in inventory
    private static List<SalesRecord> sales; // List to store all sales made


    public static JTable itemTable;

    public static void render_data() throws SQLException, IOException {
        Product.tableModel.setRowCount(0);
        loadInventory();
        for(Product product : getProducts()) {
            System.out.println(product.getName() + product.getQuantity());
            Product.tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice(), product.getQuantity()});
        }

        itemTable.repaint();


    }



    // Constructor: initializes the lists
    public InventoryManager() {
        products = new ArrayList<>(); // Initialize the products list
        sales = new ArrayList<SalesRecord>(); // Initialize the sales list
    }

    public List<Product> GetProducts() {
        return products;
    }

    // Add a product to the inventory
    public void addProduct(Product product) throws SQLException{
        products.add(product); // Add product to the list of products
        Data.addProduct(product);
        Product.tableModel.addRow(new Object[]{product.getId(), product.getName() , product.getPrice(), product.getQuantity()});
    }



    // Record a sale (decrease stock and add to sales record)
    public static void recordSale(ArrayList<ProductSale> productSales, String date) throws SQLException, IOException{
        sales.add(new SalesRecord(date, productSales));
        System.out.println("ID: " + sales.getLast().get_id());

        Data.Add_Sale(productSales, sales.getLast());

        render_data();
    }


    // Find a product by its name
    public static Product findProduct(String productName) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(productName)) {
                return p;  // Return the found product
            }
        }
        return null;  // If not found, return null
    }

    // Get the list of all products
    public static List<Product> getProducts() {
        return products;
    }

    // Get the list of all sales
    public static List<SalesRecord> getSales() {
        return sales;
    }

    // Get a list of products that are low in stock (less than 5 units)
    public List<Product> getLowStockProducts() {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getQuantity() < 5) {
                lowStock.add(p);  // Add products with low stock to the list
            }
        }
        return lowStock;
    }

    public static void loadInventory() throws SQLException, IOException {
        Categories.LoadCategories();

        products = Database.Data.getProducts();
        products.sort(Comparator.comparingInt(Product::getId));
    }


    public static int product_next_id() {
        try {
            return products.getLast().getId() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int sales_next_id() {
        try{
            return sales.getLast().get_id() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void Update_Product(Product product, String Name, float Price, int Quantity, Product_Category category) throws SQLException, IOException {
            String Previous_name = product.getName();

            products.get(products.indexOf(product)).setName(Name);
            products.get(products.indexOf(product)).setPrice(Price);
            products.get(products.indexOf(product)).setQuantity(Quantity);


            Data.update_Product(products.get(products.indexOf(product)), Previous_name);
            render_data();
    }

    public static void removeProduct(Product product) throws SQLException {
        Data.remove_Product(product.getId());
        products.remove(product);
    }
}


