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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Main.Myconnection;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.CartItem;

public class MarketController {
    @FXML private FlowPane productContainer;
    
    @FXML private TableView<CartItem> tableCart;
    @FXML private TableColumn<CartItem, String> colCartName;
    @FXML private TableColumn<CartItem, Integer> colCartQty;
    @FXML private TableColumn<CartItem, Double> colCartTotal;
    @FXML private Label lblTotal;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        loadProducts();
        colCartName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getNamePro()));
        colCartQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colCartTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());
        tableCart.setItems(cartItems);
    }

    private void loadProducts() {
    try (Connection conn = Myconnection.getConnection()) {
        String sql = "SELECT * FROM Product";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Product p = new Product(
                rs.getInt("ProductID"),
                rs.getString("NamePro"),
                rs.getString("DecriptionPro"),
                rs.getInt("CateID"),
                rs.getDouble("Price"),
                rs.getString("ImagePro") // giả sử chứa đường dẫn file ảnh, ví dụ: "images/abc.jpg"
            );

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductItem.fxml"));
            Parent card = loader.load();
            ProductItemController ctrl = loader.getController();
            ctrl.setData(p, this);
            productContainer.getChildren().add(card);
        }
    } catch (Exception e) {
        e.printStackTrace();
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
        double total = 0;
    for (CartItem item : cartItems) {
        total += item.getTotal();
    }

    if (cartItems.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText("Giỏ hàng đang trống. Không thể thanh toán.");
        alert.getDialogPane().setStyle("-fx-font-size: 18px; -fx-font-family: 'Arial';");
        alert.showAndWait();
        return;
    }

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Thanh toán thành công");
    alert.setHeaderText(null); // Ẩn dòng phân cách

    alert.setContentText(String.format("Cảm ơn bạn đã mua hàng!\nBạn đã thanh toán: %.0f ₫", total));
    alert.getDialogPane().setStyle("-fx-font-size: 18px; -fx-font-family: 'Arial';");

    alert.showAndWait();
    cartItems.clear();
    updateTotal();
    }

    @FXML
    private void onClearCart() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
        cartItems.remove(selected);
        updateTotal(); // cập nhật lại tổng tiền sau khi xóa
        } else {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText("Vui lòng chọn sản phẩm cần xóa trong giỏ hàng!");
        alert.showAndWait();
        }
    }
    
    public void addToCart(Product product, int quantity) {
    for (CartItem item : cartItems) {
        if (item.getProduct().getProductID() == product.getProductID()) {
            int newQty = item.getQuantity() + quantity;
            cartItems.set(cartItems.indexOf(item), new CartItem(product, newQty));
            updateTotal();
            return;
        }
    }
    cartItems.add(new CartItem(product, quantity));
    updateTotal();
  }

    private void updateTotal() {
    double total = 0;
    for (CartItem item : cartItems) {
        total += item.getTotal();
    }
    lblTotal.setText(String.format("Tổng tiền: %.0f ₫", total));
  }
}
