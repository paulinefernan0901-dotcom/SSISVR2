import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConfig {

    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:ssis_database.db");

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("🔧 Initializing database...");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS colleges (" +
                "code TEXT PRIMARY KEY, " +
                "name TEXT)"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS programs (" +
                "code TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "college_code TEXT, " +
                "FOREIGN KEY(college_code) REFERENCES colleges(code) ON DELETE SET NULL)"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "id TEXT PRIMARY KEY, " +
                "firstname TEXT, " +
                "lastname TEXT, " +
                "program_code TEXT, " +
                "year INTEGER, " +
                "gender TEXT, " +
                "FOREIGN KEY(program_code) REFERENCES programs(code) ON DELETE SET NULL)"
            );

            System.out.println("✅ Tables ready!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
            "Database initialization failed: " + e.getMessage());
}
        }
    }

    public static void executeUpdate(String sql, String... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }