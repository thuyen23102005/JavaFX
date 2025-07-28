package Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MarketController {
    @FXML private FlowPane productContainer;
    @FXML private TableView<?> cartTable;
    @FXML private TableColumn<?, ?> colCartName;
    @FXML private TableColumn<?, ?> colCartQty;
    @FXML private TableColumn<?, ?> colCartPrice;
    @FXML private Label lblTotal;

    @FXML
    private void onRegister() {
        System.out.println("Chuyển đến form đăng ký");
    }

    @FXML
    private void onLogin() {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
        Parent loginRoot = loader.load();

        Stage loginStage = new Stage();
        loginStage.setTitle("Đăng nhập");
        loginStage.setScene(new Scene(loginRoot));
        loginStage.setResizable(false);

        // Chặn tương tác cửa sổ chính nếu muốn
        loginStage.initModality(Modality.APPLICATION_MODAL); 

        loginStage.showAndWait(); // chờ đến khi login đóng
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
