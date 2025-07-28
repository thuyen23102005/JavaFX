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

    public void setData(Product product) {
        this.product = product;

        lblName.setText(product.getNamePro());
        lblPrice.setText(String.format("%.0f ₫", product.getPrice()));

        if (product.getImagePro() != null && !product.getImagePro().isEmpty()) {
            Image image = new Image(getClass().getResourceAsStream(product.getImagePro()));
            imgProduct.setImage(image);
        }

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        spinnerQty.setValueFactory(valueFactory);
    }

    @FXML
    private void handleAdd() {
        int qty = spinnerQty.getValue();
        System.out.println("Đã thêm vào giỏ: " + product.getNamePro() + " x" + qty);
    }
}