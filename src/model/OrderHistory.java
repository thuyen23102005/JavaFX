package model;

public class OrderHistory {
    private int orderId;
    private String dateOrder;
    private String address;

    public OrderHistory(int orderId, String dateOrder, String address) {
        this.orderId = orderId;
        this.dateOrder = dateOrder;
        this.address = address;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public String getAddress() {
        return address;
    }
}