package Controller;

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

        // Đổ dữ liệu khi click chọn dòng trong bảng
        tableProduct.setOnMouseClicked(event -> {
            Product selected = tableProduct.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtNamePro.setText(selected.getNamePro());
                txtDescription.setText(selected.getDecriptionPro());
                txtCateID.setText(String.valueOf(selected.getCateID()));
                txtPrice.setText(String.valueOf(selected.getPrice()));
                txtImage.setText(selected.getImagePro());
            }
        });
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
        String name = txtNamePro.getText().trim();
        String desc = txtDescription.getText().trim();
        String cateStr = txtCateID.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String image = txtImage.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || cateStr.isEmpty() || priceStr.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try (Connection conn = Myconnection.getConnection()) {
            String sql = "INSERT INTO Product(NamePro, DecriptionPro, CateID, Price, ImagePro) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, Integer.parseInt(cateStr));
            ps.setDouble(4, Double.parseDouble(priceStr));
            ps.setString(5, image);
            ps.executeUpdate();

            loadProducts();
            handleClear();
        } catch (NumberFormatException ex) {
            System.out.println("CateID hoặc Price không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    private void handleUpdate() {
        Product selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Chưa chọn sản phẩm để cập nhật.");
            return;
        }

        String name = txtNamePro.getText().trim();
        String desc = txtDescription.getText().trim();
        String cateStr = txtCateID.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String image = txtImage.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || cateStr.isEmpty() || priceStr.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try (Connection conn = Myconnection.getConnection()) {
            String sql = "UPDATE Product SET NamePro=?, DecriptionPro=?, CateID=?, Price=?, ImagePro=? WHERE ProductID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, Integer.parseInt(cateStr));
            ps.setDouble(4, Double.parseDouble(priceStr));
            ps.setString(5, image);
            ps.setInt(6, selected.getProductID());
            ps.executeUpdate();

            loadProducts();
            handleClear();
        } catch (NumberFormatException ex) {
            System.out.println("CateID hoặc Price không hợp lệ.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
            Path destDir = Paths.get("Images");
            Files.createDirectories(destDir);
            Path src  = selectedFile.toPath();
            Path dest = destDir.resolve(selectedFile.getName());
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            txtImage.setText(dest.toAbsolutePath().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
