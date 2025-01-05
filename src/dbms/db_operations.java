package dbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class db_operations {

    public void addExpense(String type, Date date, String description, double amount) {
        String sql = "INSERT INTO expenses (type, date, description, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = db_connection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            pstmt.setString(3, description);
            pstmt.setDouble(4, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
