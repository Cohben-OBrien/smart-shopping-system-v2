package Database;

import Records.ProductSale;
import Product.Product;
import Records.SalesRecord;
import manager.InventoryManager;

import java.sql.*;
import java.util.ArrayList;

public class Data {

    // Database connection and statement
    public static Connection connection;
    public static Statement cur;

    // Connects to the SQLite database
    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Smart shopping system/Database/Shop.sqlite");
        cur = connection.createStatement();
    }

    // Loads all users from the database
    public static ArrayList<User.User> Load_users() throws SQLException {
        connect();
        String sql = "SELECT * FROM users";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        ArrayList<User.User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User.User(
                    rs.getInt("ID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("Type")
            ));
        }
        return users;
    }

    // Retrieves all products from the database
    public static ArrayList<Product> getProducts() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM items";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getFloat("price"),
                    rs.getInt("stock")
            ));
        }

        return products;
    }

    // Gets the current number of sales (total count)
    public static int get_sale_number() throws SQLException {
        String sql = "SELECT count(*) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.getInt(1);
    }

    // Adds a new product and creates its individual sale record table
    public static void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO items (id, name, price, stock) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, product.getId());
        ps.setString(2, product.getName().replace(" ", "_"));
        ps.setFloat(3, product.getPrice());
        ps.setInt(4, product.getQuantity());
        ps.executeUpdate();

        // Create a table for tracking product-specific sales
        String name = product.getName().replace(" ", "_") + product.getId();
        String query2 = "CREATE TABLE " + name + " (sale_id INTEGER, sale_quantity INTEGER, sale_total real, FOREIGN KEY (sale_id) REFERENCES sales(id))";
        PreparedStatement ps2 = connection.prepareStatement(query2);
        ps2.executeUpdate();
    }

    // Checks if there is enough stock for a given product
    public static boolean check_stock(int id, int requied) throws SQLException {
        String sql = "SELECT stock FROM items WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();

        if (rs.getInt("stock") < requied) {
            return false;
        } else {
            return true;
        }
    }

    // Removes stock from inventory after a sale
    public static void remove_Stock(int id, int Quantity) throws SQLException {
        String Query = "SELECT stock FROM items WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(Query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int current_stock = rs.getInt("stock");

        String update = "UPDATE items SET stock = ? WHERE id = ?";
        PreparedStatement ps2 = connection.prepareStatement(update);
        ps2.setInt(1, current_stock - Quantity);
        ps2.setInt(2, id);
        ps2.executeUpdate();
    }

    // Retrieves all sales records from the sales table
    public static ArrayList<SalesRecord> getSalesRecords() throws SQLException {
        String sql = "SELECT * FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        ArrayList<SalesRecord> records = new ArrayList<>();

        while (rs.next()) {
            records.add(new SalesRecord(
                    rs.getInt("sale_ID"),
                    rs.getInt("sale_total"),
                    rs.getString("sale_date")
            ));
        }
        return records;
    }

    // Adds a new sale record and updates inventory
    public static void Add_Sale(ArrayList<ProductSale> Products, SalesRecord sale) throws SQLException {
        double total = 0;

        // Insert sale data into each product's individual sale table
        for (ProductSale productSale : Products) {
            String table = productSale.getProduct().getName().replace(" ", "_") + productSale.getProduct().getId();
            String query = "INSERT INTO " + table + " VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, sale.get_id());
            ps.setInt(2, productSale.getQuantity());
            ps.setDouble(3, productSale.getProduct().getPrice() * productSale.getQuantity());
            ps.executeUpdate();

            total += productSale.getProduct().getPrice() * productSale.getQuantity();

            // Reduce stock
            remove_Stock(productSale.getProduct().getId(), productSale.getQuantity());
        }

        // Insert the main sale into the sales table
        String query = "INSERT INTO sales VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, sale.get_id());
        ps.setDouble(2, total);
        ps.setString(3, sale.get_date());
        ps.executeUpdate();
    }

    // Gets product sale data for a specific sale
    public static ArrayList<ProductSale> getProductSales(int sale_id) throws SQLException {
        ArrayList<ProductSale> products = new ArrayList<>();

        for (Product product : InventoryManager.getProducts()) {
            String query = "SELECT * FROM " + product.getName().replace(" ", "_") + product.getId() + " WHERE sale_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, sale_id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("sale_ID") == sale_id) {
                    products.add(new ProductSale(
                            InventoryManager.findProduct(product.getName()),
                            rs.getInt("sale_quantity"),
                            rs.getInt("sale_total")
                    ));
                }
            }
        }

        return products;
    }

    // Updates an existing product and renames the related sales table if the name changed
    public static void update_Product(Product product, String Previous_name) throws SQLException {
        String sql = "UPDATE items SET name = ?, price = ?, stock = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, product.getName());
        ps.setFloat(2, product.getPrice());
        ps.setInt(3, product.getQuantity());
        ps.setInt(4, product.getId());
        ps.executeUpdate();

        // Rename product sale table if name was changed
        if (!product.getName().equals(Previous_name)) {
            String old_name = Previous_name.replace(" ", "_") + product.getId();
            String new_name = product.getName().replace(" ", "_") + product.getId();

            String query = "ALTER TABLE " + old_name + " RENAME TO " + new_name + ";";
            PreparedStatement ps2 = connection.prepareStatement(query);
            ps2.executeUpdate();
        }
    }

    // Deletes a product from the items table
    public static void remove_Product(int ID) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, ID);
        ps.executeUpdate();
    }

    // Placeholder for additional filters (if needed in the future)
    // add filter

}

