package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SigninController {

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private TextField phonenumField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;

    // Thông tin kết nối CSDL
    private final String DB_URL = "jdbc:mysql://localhost:3306/Store";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "123456";

    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phonenumField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = repeatPasswordField.getText().trim();

        if (email.isEmpty() || username.isEmpty() || phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert(AlertType.ERROR, "Mật khẩu không khớp!");
            return;
        }

        if (insertUser(email, username, phone, password)) {
            showAlert(AlertType.INFORMATION, "Đăng ký thành công!");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Đăng ký thất bại! Tên người dùng có thể đã tồn tại.");
        }
    }

    private boolean insertUser(String email, String username, String phone, String password) {
        String sql = "INSERT INTO Customer (NameCus, PhoneCus, EmailCus, UserName, Password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);   
            stmt.setString(2, phone);      // Số điện thoại người dùng nhập
            stmt.setString(3, email);
            stmt.setString(4, username);
            stmt.setString(5, password);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        emailField.clear();
        usernameField.clear();
        phonenumField.clear();
        passwordField.clear();
        repeatPasswordField.clear();
    }
}
