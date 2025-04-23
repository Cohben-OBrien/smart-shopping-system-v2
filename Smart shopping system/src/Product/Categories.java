package Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Categories {
    private static ArrayList<Product_Category> categories = new ArrayList<Product_Category>();

    public static void LoadCategories() throws SQLException, IOException {
        categories = Database.Data.LoadCategories();
    }

    public static ArrayList<Product_Category> GetCategories() {return categories;}


    public static Product_Category findCatorys(String category) {
        for(Product_Category cat: categories) {
            if(cat.getCategoryName().equals(category)) {return cat;}
        }
        return null;
    }
}
