package Product;

public class Product_Category {
    private String categoryName;
    private boolean restricted;

    public Product_Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

}
