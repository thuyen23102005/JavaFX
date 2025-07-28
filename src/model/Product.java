package model;

public class Product {
    private int productID;
    private String namePro;
    private String decriptionPro;
    private int cateID;
    private double price;
    private String imagePro;

    public Product(int productID, String namePro, String decriptionPro, int cateID, double price, String imagePro) {
        this.productID = productID;
        this.namePro = namePro;
        this.decriptionPro = decriptionPro;
        this.cateID = cateID;
        this.price = price;
        this.imagePro = imagePro;
    }

    // Getters
    public int getProductID() { return productID; }
    public String getNamePro() { return namePro; }
    public String getDecriptionPro() { return decriptionPro; }
    public int getCateID() { return cateID; }
    public double getPrice() { return price; }
    public String getImagePro() { return imagePro; }
}