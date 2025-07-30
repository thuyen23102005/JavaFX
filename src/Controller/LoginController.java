package Controller;

import java.io.IOException;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class LoginController {

    @FXML private AnchorPane si_loginForm;
    @FXML private TextField si_username;
    @FXML private PasswordField si_password;
    @FXML private Button si_loginBtn;
    @FXML private Hyperlink si_forgotPass;
    @FXML private AnchorPane su_signupForm;
    @FXML private TextField su_username;
    @FXML private PasswordField su_password;
    @FXML private ComboBox<?> su_question;
    @FXML private TextField su_answer;
    @FXML private Button su_signupBtn;
    @FXML private TextField fp_username;
    @FXML private AnchorPane fp_questionForm;
    @FXML private Button fp_proceedBtn;
    @FXML private ComboBox<?> fp_question;
    @FXML private TextField fp_answer;
    @FXML private Button fp_back;
    @FXML private AnchorPane np_newPassForm;
    @FXML private PasswordField np_newPassword;
    @FXML private PasswordField np_confirmPassword;
    @FXML private Button np_changePassBtn;
    @FXML private Button np_back;
    @FXML private AnchorPane side_form;
    @FXML private Button side_CreateBtn;
    @FXML private Button side_alreadyHave;
    @FXML private TextField Username;
    @FXML private PasswordField Password;
    @FXML private Button BTLogin;
    @FXML private Button BTSignin;

    // Thông tin kết nối CSDL
    private final String DB_URL = "jdbc:mysql://localhost:3306/Store";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "123456";

    @FXML
    private void initialize() {
        BTLogin.setOnAction(event -> handleLogin());
        BTSignin.setOnAction(event -> handleSignIn());
    }

    private void handleLogin() {
    String username = Username.getText();
    String password = Password.getText();

    if (username.isEmpty() || password.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
        return;
    }

    // Lấy ID và tên người dùng
    String sql = "SELECT IDCus, NameCus FROM Customer WHERE UserName = ? AND Password = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int customerId = rs.getInt("IDCus");
            String nameCus = rs.getString("NameCus");

            // Load giao diện Market.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Market.fxml"));
            Parent root = loader.load();

            // Gửi dữ liệu sang controller
            Controller.MarketController controller = loader.getController();
            controller.setCurrentCustomerId(customerId);
            controller.setCustomerName(nameCus);

            // Chuyển màn
            Stage stage = (Stage) BTLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Market");
            stage.show();
        } else {
            showAlert(Alert.AlertType.ERROR, "Sai tên đăng nhập hoặc mật khẩu!");
        }

    } catch (SQLException | IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Lỗi khi đăng nhập.");
    }
  }

    private int getCustomerId(String username, String password) {
        String sql = "SELECT IDCus FROM Customer WHERE UserName = ? AND Password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDCus");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void handleSignIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Signin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) BTSignin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng ký tài khoản");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Không thể mở giao diện đăng ký!");
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
