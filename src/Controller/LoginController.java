/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;

public class LoginController {

    @FXML private TextField Username;
    @FXML private PasswordField Password;
    @FXML private Button BTLogin;
    @FXML private Button BTSignin;

    // Thông tin kết nối MySQL (bạn thay bằng database thật của bạn)
    private final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "your_password";

    @FXML
    private void initialize() {
        BTLogin.setOnAction(event -> handleLogin());
        BTSignin.setOnAction(event -> handleSignIn());
    }

    private void handleLogin() {
        String username = Username.getText();
        String password = Password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
            return;
        }

        if (checkLogin(username, password)) {
            showAlert(AlertType.INFORMATION, "Đăng nhập thành công! Chào " + username + "!");
        } else {
            showAlert(AlertType.ERROR, "Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void handleSignIn() {
        showAlert(AlertType.INFORMATION, "Tính năng đăng ký chưa được triển khai.");
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
