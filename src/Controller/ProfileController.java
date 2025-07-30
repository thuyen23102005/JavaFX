package Controller;

import Main.Myconnection;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.OrderHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.OrderDetailItem;

public class ProfileController {

    @FXML private Label lblName;
    @FXML private Label lblPhone;
    @FXML private Label lblEmail;
    @FXML private Label lblUsername;

    @FXML private TableView<OrderHistory> tableHistory;
    @FXML private TableColumn<OrderHistory, Integer> colOrderId;
    @FXML private TableColumn<OrderHistory, String> colDateOrder;
    @FXML private TableColumn<OrderHistory, String> colAddress;

    @FXML private TableView<OrderDetailItem> tableDetail;
    @FXML private TableColumn<OrderDetailItem, String> colProduct;
    @FXML private TableColumn<OrderDetailItem, Integer> colQuantity;
    @FXML private TableColumn<OrderDetailItem, Double> colPrice;
    @FXML private TableColumn<OrderDetailItem, Double> colTotal;

    @FXML private Button btnLogin;
    @FXML private Button btnProducts;

    private int currentCustomerId = -1;

    private ObservableList<OrderHistory> orderList = FXCollections.observableArrayList();
    private ObservableList<OrderDetailItem> detailList = FXCollections.observableArrayList();

    public void setCustomerId(int id) {
        this.currentCustomerId = id;
        loadCustomerInfo();
        loadOrderHistory();
    }

    @FXML
    private void initialize() {
        tableHistory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadOrderDetail(newVal.getOrderId());
            }
        });
    }

    @FXML
    private void onLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGoToMarket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Market.fxml"));
            Parent root = loader.load();

            MarketController controller = loader.getController();
            controller.setCurrentCustomerId(currentCustomerId);

            Stage stage = (Stage) btnProducts.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Market");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerInfo() {
    try (Connection conn = Myconnection.getConnection()) {
        String sql = "SELECT * FROM Customer WHERE IDCus = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, currentCustomerId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            lblName.setText("Tên: " + rs.getString("NameCus"));
            lblPhone.setText("SĐT: " + rs.getString("PhoneCus"));
            lblEmail.setText("Email: " + rs.getString("EmailCus"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void loadOrderHistory() {
        orderList.clear();
        try (Connection conn = Myconnection.getConnection()) {
            String sql = """
                SELECT od.IDOrder, op.DateOrder, op.AddressDeliverry
                FROM OrderDetail od
                JOIN OrderPro op ON od.IDOrder = op.ID
                WHERE op.IDCus = ?
                GROUP BY od.IDOrder, op.DateOrder, op.AddressDeliverry
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentCustomerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("IDOrder");
                String date = rs.getString("DateOrder");
                String address = rs.getString("AddressDeliverry");
                orderList.add(new OrderHistory(orderId, date, address));
            }

            colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            colDateOrder.setCellValueFactory(new PropertyValueFactory<>("dateOrder"));
            colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            tableHistory.setItems(orderList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrderDetail(int orderId) {
    ObservableList<OrderDetailItem> detailList = FXCollections.observableArrayList();
    try (Connection conn = Myconnection.getConnection()) {
        String sql = """
            SELECT od.Quantity, od.UnitPrice, (od.Quantity * od.UnitPrice) AS Total,
                   p.NamePro AS ProductName
            FROM OrderDetail od
            JOIN Product p ON od.IDProduct = p.ProductID
            WHERE od.IDOrder = ?
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String product = rs.getString("ProductName");
            int quantity = rs.getInt("Quantity");
            double price = rs.getDouble("UnitPrice");
            double total = rs.getDouble("Total");
            detailList.add(new OrderDetailItem(product, quantity, price, total));
        }

        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tableDetail.setItems(detailList);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML
    private void onDeleteOrder() {
        OrderHistory selected = tableHistory.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = Myconnection.getConnection()) {
                String deleteDetail = "DELETE FROM OrderDetail WHERE IDOrder = ?";
                PreparedStatement stmtDetail = conn.prepareStatement(deleteDetail);
                stmtDetail.setInt(1, selected.getOrderId());
                stmtDetail.executeUpdate();

                String deleteOrder = "DELETE FROM OrderPro WHERE ID = ?";
                PreparedStatement stmtOrder = conn.prepareStatement(deleteOrder);
                stmtOrder.setInt(1, selected.getOrderId());
                stmtOrder.executeUpdate();

                orderList.remove(selected);
                detailList.clear();
                showAlert(Alert.AlertType.INFORMATION, "Đã xoá đơn hàng thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi khi xoá đơn hàng!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn đơn hàng cần xoá.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
