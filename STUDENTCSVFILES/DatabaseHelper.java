import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {

    static String[] firstNames = {
        "James","John","Michael","David","Chris","Daniel","Mark",
        "Joseph","Andrew","Matthew","Joshua","Ryan","Kyle","Paul",
        "Angela","Maria","Nicole","Sarah","Grace","Anna","Emma","Sophia"
};

    static String[] lastNames = {
        "Reyes","Santos","Cruz","Garcia","Bautista","Dela Cruz",
        "Torres","Mendoza","Aquino","Villanueva","Navarro","Lim",
        "Rivera","Castro","Flores"
};
    

   public static Connection connect() {
    try {
        String url = "jdbc:sqlite:C:/Users/Aspire 3/Downloads/STUDENTCSVFILES/school.db";

        System.out.println("CONNECTING TO: " + url);

        return DriverManager.getConnection(url);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


    public static void initializeDatabase() {

        String studentTable = "CREATE TABLE IF NOT EXISTS student (" +
                "id TEXT PRIMARY KEY," +
                "firstname TEXT," +
                "lastname TEXT," +
                "program_code TEXT," +
                "year INTEGER," +
                "gender TEXT)";

        String programTable = "CREATE TABLE IF NOT EXISTS program (" +
                "code TEXT PRIMARY KEY," +
                "name TEXT," +
                "college TEXT)";

        String collegeTable = "CREATE TABLE IF NOT EXISTS college (" +
                "code TEXT PRIMARY KEY," +
                "name TEXT)";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {

            stmt.execute(studentTable);
            stmt.execute(programTable);
            stmt.execute(collegeTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public static void generateStudents(DefaultTableModel model) {

            model.setRowCount(0);

            String[] programs = {"BSCS", "BSIT", "BSIS", "BSBA", "BSECE"};
            String[] genders = {"Male", "Female"};

            for (int i = 1; i <= 5000; i++) {

                String id = String.format("2026-%04d", i);

                String first = firstNames[(int)(Math.random() * firstNames.length)];
                String last = lastNames[(int)(Math.random() * lastNames.length)];

                model.addRow(new Object[]{
                        id,
                        first,
                        last,
                        programs[(int)(Math.random() * programs.length)],
                        (int)(Math.random() * 4) + 1,
                        genders[(int)(Math.random() * 2)]
                });
            }

            System.out.println("Generated 5000 realistic students.");
        }

  public static void generatePrograms(DefaultTableModel model) {

        model.setRowCount(0);

        String[] basePrograms = {
                "BSCS", "BSIT", "BSIS", "BSCE", "BSECE", "BSBA",
                "BSHM", "BSTM", "BSA", "BSN", "BSEE"
        };

        String[] programNames = {
                "Computer Science",
                "Information Technology",
                "Information Systems",
                "Civil Engineering",
                "Electronics Engineering",
                "Business Administration",
                "Hospitality Management",
                "Tourism Management",
                "Accountancy",
                "Nursing",
                "Electrical Engineering"
        };

        String[] colleges = {"CCS", "COE", "CBA", "CHS"};

        int count = 0;

        for (int i = 0; i < basePrograms.length; i++) {

            // create 2–3 variations per program → reaches ~30
            for (int j = 1; j <= 3; j++) {

                if (count >= 30) break;

                String code = basePrograms[i] + j;
                String name = programNames[i] + " Major " + j;
                String college = colleges[(int)(Math.random() * colleges.length)];

                model.addRow(new Object[]{
                        code,
                        name,
                        college
                });

                count++;
            }
        }

        System.out.println("Generated " + count + " programs (30 required).");
    }

    // 👇 PASTE YOUR METHOD HERE
    public static void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);

        String sql = "SELECT * FROM student";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("program_code"),
                    rs.getInt("year"),
                    rs.getString("gender")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

            public static void addStudent(String id, String firstname, String lastname,
                                    String program, int year, String gender) {

            String sql = "INSERT INTO student VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, id);
                ps.setString(2, firstname);
                ps.setString(3, lastname);
                ps.setString(4, program);
                ps.setInt(5, year);
                ps.setString(6, gender);

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void updateStudent(String id, String firstname, String lastname, String program, int year, String gender) {

            String sql = "UPDATE student SET firstname=?, lastname=?, program_code=?, year=?, gender=? WHERE id=?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, firstname);
                ps.setString(2, lastname);
                ps.setString(3, program);
                ps.setInt(4, year);
                ps.setString(5, gender);
                ps.setString(6, id);

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void searchStudents(DefaultTableModel model, String keyword, String field) {

            model.setRowCount(0);

            String sql = "SELECT * FROM student WHERE " + field + " LIKE ?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, "%" + keyword + "%");

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("program_code"),
                        rs.getInt("year"),
                        rs.getString("gender")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void deleteStudent(String id) {

            String sql = "DELETE FROM student WHERE id=?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, id);
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void loadPrograms(DefaultTableModel model) {

                model.setRowCount(0);

                String sql = "SELECT * FROM program";

                try (Connection conn = connect();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                    while (rs.next()) {

                        String code = rs.getString(1);
                        String name = rs.getString(2);
                        String college = rs.getString(3);

                        model.addRow(new Object[]{code, name, college});
                    }

                } catch (Exception e) {
                    System.out.println("ERROR IN loadPrograms()");
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                            "Error loading programs:\n" + e.getMessage());
                }
            }
            public static void addProgram(String code, String name, String college) {

                String sql = "INSERT INTO program VALUES (?, ?, ?)";

                try (Connection conn = connect();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, code);
                    ps.setString(2, name);
                    ps.setString(3, college);

                    ps.executeUpdate();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public static void updateProgram(String code, String name, String college) {

                String sql = "UPDATE program SET name=?, college=? WHERE code=?";

                try (Connection conn = connect();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, name);
                    ps.setString(2, college);
                    ps.setString(3, code);

                    ps.executeUpdate();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public static void deleteProgram(String code) {

                String sql = "DELETE FROM program WHERE code=?";

                try (Connection conn = connect();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, code);
                    ps.executeUpdate();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public static void searchPrograms(DefaultTableModel model, String keyword, String field) {

                model.setRowCount(0);

                String sql = "SELECT * FROM program WHERE " + field + " LIKE ?";

                try (Connection conn = connect();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, "%" + keyword + "%");

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("college")
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
}


        public static void loadColleges(DefaultTableModel model) {
            model.setRowCount(0);

            String sql = "SELECT * FROM college";

            try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("name")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void addCollege(String code, String name) {

            String sql = "INSERT INTO college VALUES (?, ?)";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, code);
                ps.setString(2, name);

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void updateCollege(String code, String name) {

            String sql = "UPDATE college SET name=? WHERE code=?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, code);

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void deleteCollege(String code) {

            String sql = "DELETE FROM college WHERE code=?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, code);
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void searchColleges(DefaultTableModel model, String keyword, String field) {

            model.setRowCount(0);

            String sql = "SELECT * FROM college WHERE " + field + " LIKE ?";

            try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, "%" + keyword + "%");

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("name")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
 }