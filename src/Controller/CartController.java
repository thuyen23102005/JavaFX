/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author ngoth
 */
public class CartController implements Initializable {

    @FXML
private TableView<CartItem> cartTable;
@FXML
private TableColumn<CartItem, String> productNameCol;
@FXML
private TableColumn<CartItem, Integer> quantityCol;
@FXML
private TableColumn<CartItem, Double> priceCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Button checkoutButton;
    
    @Override
public void initialize(URL url, ResourceBundle rb) {
    // Gán dữ liệu cột từ thuộc tính CartItem
    productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
    quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

    // Tạo dữ liệu mẫu
    ObservableList<CartItem> items = FXCollections.observableArrayList(
        new CartItem("Táo", 2, 10000),
        new CartItem("Chuối", 3, 8000),
        new CartItem("Cam", 1, 15000)
    );

    // Gán dữ liệu vào bảng
    cartTable.setItems(items);

    // Tính tổng tiền
    double total = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    totalLabel.setText(String.format("%.0f VND", total));
}
}
