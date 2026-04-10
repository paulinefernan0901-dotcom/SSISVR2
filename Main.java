import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Load SQLite driver (safe to keep)
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Add the JAR to libraries.");
            return;
        }

        // Initialize database ONCE (use your DatabaseConfig)
        DatabaseConfig.initialize();

        // Start GUI
        SwingUtilities.invokeLater(() -> {
            new StudentGUI().setVisible(true);
        });
    }
}