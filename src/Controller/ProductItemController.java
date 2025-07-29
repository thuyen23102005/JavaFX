package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.SpinnerValueFactory;
import model.Product;

public class ProductItemController {

    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Spinner<Integer> spinnerQty;

    private Product product;
    private MarketController marketController;

    public void setData(Product product, MarketController controller) {
    this.product = product;
    this.marketController = controller;

    lblName.setText(product.getNamePro());
    lblPrice.setText(String.format("%.0f ₫", product.getPrice()));

    try {
        String imagePath = product.getImagePro();
        Image image;

        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("/") || imagePath.contains("images")) {
                image = new Image(getClass().getResourceAsStream(imagePath));
            } else {
                image = new Image("file:" + imagePath);
            }
        } else {
            image = new Image(getClass().getResourceAsStream("/images/default.png"));
        }

        imgProduct.setImage(image);
    } catch (Exception e) {
        System.err.println("Không thể tải ảnh: " + product.getImagePro());
        e.printStackTrace();
        imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
    }

    SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
    spinnerQty.setValueFactory(valueFactory);
}

    @FXML
    private void handleAdd() {
        int qty = spinnerQty.getValue();
        if (marketController != null) {
        marketController.addToCart(product, qty);
        }
    }
}