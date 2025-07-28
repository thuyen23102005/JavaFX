/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package Controller;

<<<<<<< HEAD
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
=======
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;
>>>>>>> refs/remotes/origin/master

public class LoginController {

<<<<<<< HEAD
    @FXML
    private AnchorPane si_loginForm;
    
    @FXML
    private TextField si_username;
    
    @FXML
    private PasswordField si_password;
    
    @FXML
    private Button si_loginBtn;
    
    @FXML
    private Hyperlink si_forgotPass;
    
    @FXML
    private AnchorPane su_signupForm;
    
    @FXML
    private TextField su_username;
    
    @FXML
    private PasswordField su_password;
    
    @FXML
    private ComboBox<?> su_question;
    
    @FXML
    private TextField su_answer;
    
    @FXML
    private Button su_signupBtn;
    
    @FXML
    private TextField fp_username;
    
    @FXML
    private AnchorPane fp_questionForm;
    
    @FXML
    private Button fp_proceedBtn;
    
    @FXML
    private ComboBox<?> fp_question;
    
    @FXML
    private TextField fp_answer;
    
    @FXML
    private Button fp_back;
    
    @FXML
    private AnchorPane np_newPassForm;
    
    @FXML
    private PasswordField np_newPassword;
    
    @FXML
    private PasswordField np_confirmPassword;
    
    @FXML
    private Button np_changePassBtn;
    
    @FXML
    private Button np_back;
    
    @FXML
    private AnchorPane side_form;
    
    @FXML
    private Button side_CreateBtn;
    
    @FXML
    private Button side_alreadyHave;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
=======
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
>>>>>>> refs/remotes/origin/master
}
