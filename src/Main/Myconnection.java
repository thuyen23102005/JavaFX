/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.sql.*;
/**
 *
 * @author ngoth
 */
public class Myconnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/QLDB";
            String user = "root"; // thay đổi nếu khác
            String pass = "123";     // nhập password MySQL nếu có
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
