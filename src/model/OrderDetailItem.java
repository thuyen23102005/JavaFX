package model;

public class OrderDetailItem {
    private String productName;
    private int quantity;
    private double price;
    private double total;

    public OrderDetailItem(String productName, int quantity, double price, double total) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    // Thêm getter để TableView đọc được dữ liệu
    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }
}