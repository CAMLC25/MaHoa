package Mahoa;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test";

    static final String USER = "username";
    static final String PASS = "password";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            // Tạo bảng user nếu chưa tồn tại
            String sql = "CREATE TABLE IF NOT EXISTS users " +
                         "(id INTEGER not NULL AUTO_INCREMENT, " +
                         " username VARCHAR(255), " + 
                         " password VARCHAR(255), " + 
                         " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
            
            while (true) {
                System.out.println("1. Đăng ký");
                System.out.println("2. Đăng nhập");
                System.out.println("3. Thoát");
                System.out.print("Chọn: ");
                int choice = scanner.nextInt();
                
                if (choice == 1) {
                    register(conn);
                } else if (choice == 2) {
                    login(conn);
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            }

            stmt.close();
            conn.close();
            scanner.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            }
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            }
        }
    }
    
    static void register(Connection conn) throws SQLException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập tên người dùng: ");
        String username = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);
        
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement registerStatement = conn.prepareStatement(sql);
        registerStatement.setString(1, username);
        registerStatement.setString(2, hashedPassword);
        registerStatement.executeUpdate();
        
        System.out.println("Đăng ký thành công.");
    }
    
    static void login(Connection conn) throws SQLException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập tên người dùng: ");
        String username = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);
        
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement loginStatement = conn.prepareStatement(sql);
        loginStatement.setString(1, username);
        loginStatement.setString(2, hashedPassword);
        ResultSet rs = loginStatement.executeQuery();
        
        if(rs.next()) {
            System.out.println("Đăng nhập thành công. Chào mừng, " + username + "!");
        } else {
            System.out.println("Tên người dùng hoặc mật khẩu không chính xác.");
        }
        
        rs.close();
    }
    
    static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] hashedBytes = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
}
