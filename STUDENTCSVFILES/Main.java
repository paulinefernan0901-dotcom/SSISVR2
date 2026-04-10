public class Main {
    public static void main(String[] args) {

        DatabaseHelper.initializeDatabase();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new StudentGUI().setVisible(true);
        });
    }
}