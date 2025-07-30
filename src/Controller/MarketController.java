package Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import model.Product;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Main.Myconnection;
import java.sql.Statement;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CartItem;

public class MarketController {
    @FXML private FlowPane productContainer;
    
    @FXML private TableView<CartItem> tableCart;
    @FXML private TableColumn<CartItem, String> colCartName;
    @FXML private TableColumn<CartItem, Integer> colCartQty;
    @FXML private TableColumn<CartItem, Double> colCartTotal;
    @FXML private Label lblTotal;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    
    @FXML private Label lblWelcome;
    
    public void setCustomerName(String name) {
    lblWelcome.setText("Welcome, " + name);
    }
    
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
    
    @FXML private Button btnProfile;
    
    @FXML
    private void onProfile() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Profile.fxml"));
        Parent root = loader.load();

        ProfileController controller = loader.getController();
        controller.setCustomerId(currentCustomerId);

        Stage stage = (Stage) btnProfile.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Thông tin cá nhân");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
    
    public void setCurrentCustomerId(int id) {
    this.currentCustomerId = id;
    }
    
    private int currentCustomerId;
    
   @FXML
   private void onCheckout() {
    if (cartItems.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText("Giỏ hàng đang trống. Không thể thanh toán.");
        alert.getDialogPane().setStyle("-fx-font-size: 18px; -fx-font-family: 'Arial';");
        alert.showAndWait();
        return;
    }

    try (Connection conn = Myconnection.getConnection()) {
        conn.setAutoCommit(false); // Bắt đầu transaction

        // 1. Thêm vào OrderPro
        String insertOrder = "INSERT INTO OrderPro (DateOrder, IDCus, AddressDeliverry) VALUES (?, ?, ?)";
        PreparedStatement orderStmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
        orderStmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
        orderStmt.setInt(2, currentCustomerId); // ID khách hàng (hiện đang gán cố định)
        orderStmt.setString(3, "Địa chỉ mặc định"); // sau này bạn có thể thêm giao diện nhập địa chỉ
        orderStmt.executeUpdate();

        // 2. Lấy ID đơn hàng vừa tạo
        ResultSet rs = orderStmt.getGeneratedKeys();
        int orderId = -1;
        if (rs.next()) {
            orderId = rs.getInt(1);
        } else {
            throw new SQLException("Không thể lấy ID của đơn hàng.");
        }

        // 3. Thêm chi tiết vào OrderDetail
        String insertDetail = "INSERT INTO OrderDetail (IDProduct, IDOrder, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        PreparedStatement detailStmt = conn.prepareStatement(insertDetail);

        for (CartItem item : cartItems) {
            detailStmt.setInt(1, item.getProduct().getProductID());
            detailStmt.setInt(2, orderId);
            detailStmt.setInt(3, item.getQuantity());
            detailStmt.setDouble(4, item.getProduct().getPrice());
            detailStmt.addBatch();
        }

        detailStmt.executeBatch(); // Thêm tất cả 1 lần
        conn.commit(); // Thành công => commit

        // 4. Hiển thị thông báo và xóa giỏ hàng
        double total = getTotal();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh toán thành công");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Cảm ơn bạn đã mua hàng!\nBạn đã thanh toán: %.0f ₫", total));
        alert.getDialogPane().setStyle("-fx-font-size: 18px; -fx-font-family: 'Arial';");
        alert.showAndWait();

        cartItems.clear();
        updateTotal();

    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Lỗi khi thanh toán: " + e.getMessage());
    }
   } 
   
   @FXML
    private void onAddSP() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Pro_Crud.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Thêm / Sửa / Xóa Sản phẩm");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Không thể mở cửa sổ Thêm Sản phẩm!");
    }
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
    private double getTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
          total += item.getTotal();
        }
        return total;
    }

    private void showAlert(Alert.AlertType type, String message) {
       Alert alert = new Alert(type);
       alert.setTitle("Thông báo");
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
    }
}
