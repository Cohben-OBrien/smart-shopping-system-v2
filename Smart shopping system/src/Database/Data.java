package Database;

import Records.ProductSale;
import Product.Product;
import Records.SalesRecord;
import manager.InventoryManager;

import java.sql.*;
import java.util.ArrayList;

public class Data {
    public static Connection connection;
    public static Statement cur;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Smart shopping system/Database/Shop.sqlite");
        cur =  connection.createStatement();

    }

    public static ArrayList<User.User> Load_users() throws SQLException {
        connect();
        System.out.println(connection);
        String sql = "SELECT * FROM users";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeQuery();
        ResultSet rs = ps.executeQuery();
        System.out.println(rs);
        ArrayList<User.User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User.User(rs.getInt("ID"), rs.getString("Username"), rs.getString("Password"), rs.getString("Type")));
        }
        return users;
    }

    public static ArrayList<Product> getProducts() throws SQLException {

        ArrayList<Product> products = new ArrayList<>();

        String query = "SELECT * FROM items";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            products.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"), rs.getInt("stock")));
        }

        return products;
    }


    public static int get_sale_number() throws SQLException {
        String sql = "SELECT count(*) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();


        return rs.getInt(1);
    }

   public static void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO items (id, name, price, stock) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, product.getId());
        ps.setString(2, product.getName().replace(" ", "_"));
        ps.setFloat(3, product.getPrice());
        ps.setInt(4, product.getQuantity());

        ps.executeUpdate();

        String name = product.getName().replace(" ", "_") + product.getId();
        String query2 = "CREATE TABLE " + name + " (sale_id INTEGER, sale_quantity INTEGER, sale_total real, FOREIGN KEY (sale_id) REFERENCES sales(id))";
        PreparedStatement ps2 = connection.prepareStatement(query2);
        ps2.executeUpdate();


    }

    public static boolean check_stock(int id, int requied) throws SQLException {
        String sql = "SELECT stock FROM items WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();

        System.out.println("required" + requied);
        System.out.println("database" + rs.getInt("stock"));


        if (rs.getInt("stock") < requied) {
            System.out.println("Not enough stock");
            return false;
        } else {
            System.out.println("Enough stock");
            return true;
        }
    }

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

        System.out.println("Stock removed, new quantity = " + (current_stock - Quantity));
    }

    public static ArrayList<SalesRecord> getSalesRecords() throws SQLException {
        String sql = "SELECT * FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        ArrayList<SalesRecord> records = new ArrayList<>();
        while (rs.next()) {
            records.add(new SalesRecord(rs.getInt("sale_ID"), rs.getInt("sale_total"), rs.getString("sale_date")));
        }
        return records;
    }



    public static void Add_Sale(ArrayList<ProductSale> Products, SalesRecord sale) throws SQLException {
        double total = 0;

        for (ProductSale productSale : Products) {
            String table = productSale.getProduct().getName().replace(" ", "_") + productSale.getProduct().getId();
            String query = "INSERT INTO " + table + " VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, sale.get_id());
            ps.setInt(2, productSale.getQuantity());
            ps.setDouble(3, productSale.getProduct().getPrice() * productSale.getQuantity());
            ps.executeUpdate();
            total = total + (productSale.getProduct().getPrice() * productSale.getQuantity());

            remove_Stock(productSale.getProduct().getId(), productSale.getQuantity());
        }



        System.out.println("total" + total);


        String query = "INSERT INTO sales VALUES (?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, sale.get_id());
        ps.setDouble(2, total);
        ps.setString(3, sale.get_date());
        ps.executeUpdate();

    }

    public static ArrayList<ProductSale> getProductSales(int sale_id) throws SQLException {
        ArrayList<ProductSale> products = new ArrayList<>();

        for(Product product : InventoryManager.getProducts()) {
            String query = "SELECT * FROM " + product.getName() + product.getId() + " WHERE sale_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, sale_id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if(rs.getInt("sale_ID") == sale_id) {
                    System.out.println(product.getName() + "_" + product.getId());
                    products.add(new ProductSale(InventoryManager.findProduct(product.getName()), rs.getInt("sale_quantity"), rs.getInt("sale_total")));
                }

            }
        }
        return products;
    }

    public static void update_Product(Product product, String Previous_name) throws SQLException {

        String sql = "UPDATE items SET name = ?, price = ?, stock = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, product.getName());
        ps.setFloat(2, product.getPrice());
        ps.setInt(3, product.getQuantity());
        ps.setInt(4, product.getId());
        ps.executeUpdate();


        System.out.println(Previous_name);

        if(!product.getName().equals(Previous_name)) {

            String old_name = Previous_name.replace(" ", "_")+product.getId();
            String new_name = product.getName().replace(" ", "_")+product.getId();

            String query = "ALTER TABLE "+ old_name + " RENAME TO " + new_name + ";";
            PreparedStatement ps2 = connection.prepareStatement(query);
            ps2.executeUpdate();
        }
    }

    public static void remove_Product(int ID) throws SQLException {
        String sql = "DELETE FROM sales WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, ID);
        ps.executeUpdate();
    }

   //add filter

}
