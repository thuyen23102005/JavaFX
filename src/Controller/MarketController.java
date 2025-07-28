package Controller;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import model.Product;
import java.util.*;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MarketController {
    @FXML private FlowPane productContainer;
    
    @FXML
    private void initialize() {
        loadProducts();
    }

    private void loadProducts() {
    List<Product> products = Arrays.asList(
        new Product(1, "Trà sữa", "Ngon", 1, 25000, "/images/watermelon.png"),
        new Product(2, "Bánh ngọt", "Thơm", 1, 15000, "/images/orange.png"),
        new Product(3, "Cà phê", "Đậm đà", 1, 20000, "/images/kiwi.png")
    );

    for (Product p : products) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductItem.fxml"));
            Parent card = loader.load();
            ProductItemController ctrl = loader.getController();
            ctrl.setData(p);
            productContainer.getChildren().add(card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    @FXML
    private Button btnLogin;

    @FXML
    private void onLogin() {
        try {
          Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
          Stage stage = (Stage) btnLogin.getScene().getWindow(); // dùng btnLogin để lấy Stage hiện tại
          stage.setScene(new Scene(root));
          stage.setTitle("Đăng nhập");
          stage.show();
       } catch (IOException e) {
        e.printStackTrace();
       }
    }

    @FXML
    private void onCheckout() {
        System.out.println("Xử lý thanh toán");
    }

    @FXML
    private void onClearCart() {
        System.out.println("Xoá toàn bộ giỏ hàng");
    }

    @FXML
    private void onPrint() {
        System.out.println("In hóa đơn");
    }
}
