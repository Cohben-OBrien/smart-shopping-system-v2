package Records;

public class ProductSale {
    private smartshop.Product product;
    private int quantity;
    private double total;
    private int saleId;

    public ProductSale(smartshop.Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.total = product.getPrice() * quantity;
        this.saleId = 1; // temp
    }






}
