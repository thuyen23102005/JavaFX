/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author thuyen
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;
import java.io.File;

import model.Product;
import Main.Myconnection;
import javafx.scene.image.ImageView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.stage.FileChooser;
import java.io.File;

public class ProductController implements Initializable {
    @FXML private TextField txtNamePro, txtDescription, txtCateID, txtPrice, txtImage;
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, Integer> colID;
    @FXML private TableColumn<Product, String> colName, colDesc;
    @FXML private TableColumn<Product, ImageView> colImage;
    @FXML private TableColumn<Product, Integer> colCate;
    @FXML private TableColumn<Product, Double> colPrice;

    ObservableList<Product> listProduct = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("namePro"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("decriptionPro"));
        colCate.setCellValueFactory(new PropertyValueFactory<>("cateID"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colImage.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().getImageView()));
        
        loadProducts();
    }

    private void loadProducts() {
        listProduct.clear();
        try (Connection conn = Myconnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product");
            while (rs.next()) {
                listProduct.add(new Product(
                    rs.getInt("ProductID"),
                    rs.getString("NamePro"),
                    rs.getString("DecriptionPro"),
                    rs.getInt("CateID"),
                    rs.getDouble("Price"),
                    rs.getString("ImagePro")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        tableProduct.setItems(listProduct);
    }

    @FXML 
    private void handleAdd() {
        String sql = "INSERT INTO Product(NamePro, DecriptionPro, CateID, Price, ImagePro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Myconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtNamePro.getText());
            ps.setString(2, txtDescription.getText());
            ps.setInt(3, Integer.parseInt(txtCateID.getText()));
            ps.setDouble(4, Double.parseDouble(txtPrice.getText()));
            ps.setString(5, txtImage.getText());
            ps.executeUpdate();
            loadProducts();
            handleClear();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML 
    private void handleUpdate() {
        Product selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql = "UPDATE Product SET NamePro=?, DecriptionPro=?, CateID=?, Price=?, ImagePro=? WHERE ProductID=?";
        try (Connection conn = Myconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtNamePro.getText());
            ps.setString(2, txtDescription.getText());
            ps.setInt(3, Integer.parseInt(txtCateID.getText()));
            ps.setDouble(4, Double.parseDouble(txtPrice.getText()));
            ps.setString(5, txtImage.getText());
            ps.setInt(6, selected.getProductID());
            ps.executeUpdate();
            loadProducts();
            handleClear();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML 
    private void handleDelete() {
        Product selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql = "DELETE FROM Product WHERE ProductID=?";
        try (Connection conn = Myconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selected.getProductID());
            ps.executeUpdate();
            loadProducts();
            handleClear();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML 
    private void handleClear() {
        txtNamePro.clear(); txtDescription.clear(); txtCateID.clear();
        txtPrice.clear(); txtImage.clear();
    }
    
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Ảnh (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile == null) return;

    try {
        // Tạo thư mục images nếu chưa có
        Path destDir = Paths.get("Images");
        Files.createDirectories(destDir);

        // Copy file vào thư mục Images
        Path src  = selectedFile.toPath();
        Path dest = destDir.resolve(selectedFile.getName());
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

        // Lưu đường dẫn tuyệt đối (hoặc tương đối tuỳ bạn muốn)
        txtImage.setText(dest.toAbsolutePath().toString());

        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }
}