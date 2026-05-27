import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {

    
    public static Connection connect() {//Java to Sqlite database
        try {
            
            String url = "jdbc:sqlite:school.db";//calls school.db
            return DriverManager.getConnection(url);//opens the connection to the database
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //INITIALIZES AND CREATES TABLE IF IT DOES NOT EXIST YET
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

            stmt.execute(studentTable);//run sql command
            stmt.execute(programTable);
            stmt.execute(collegeTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generateSampleData() {

        try (Connection conn = connect()) {

            conn.setAutoCommit(false); 

            //INSERT 30 PROGARMS ─
            String checkProg = "SELECT COUNT(*) FROM program";
            ResultSet rs = conn.createStatement().executeQuery(checkProg);
            int existingPrograms = rs.getInt(1);

            if (existingPrograms == 0) {
                PreparedStatement psProg = conn.prepareStatement(
                        "INSERT OR IGNORE INTO program VALUES (?, ?, ?)");
                for (String[] p : programData) {
                    psProg.setString(1, p[0]);
                    psProg.setString(2, p[1]);
                    psProg.setString(3, p[2]);
                    psProg.addBatch();
                }
                psProg.executeBatch();
            }

            //INSERT 5K STUDENTS
            String checkStud = "SELECT COUNT(*) FROM student";
            ResultSet rs2 = conn.createStatement().executeQuery(checkStud);
            int existingStudents = rs2.getInt(1);

            if (existingStudents == 0) {
                String[] programCodes = new String[programData.length];
                for (int i = 0; i < programData.length; i++) programCodes[i] = programData[i][0];

                String[] genders = {"Male", "Female"};

                PreparedStatement psStud = conn.prepareStatement(
                        "INSERT OR IGNORE INTO student VALUES (?, ?, ?, ?, ?, ?)");

                for (int i = 1; i <= 5000; i++) {
                    String id = String.format("2026-%04d", i);
                    String first = firstNames[(int)(Math.random() * firstNames.length)];
                    String last  = lastNames[(int)(Math.random() * lastNames.length)];
                    String prog  = programCodes[(int)(Math.random() * programCodes.length)];
                    int year     = (int)(Math.random() * 4) + 1;
                    String gender = genders[(int)(Math.random() * 2)];

                    psStud.setString(1, id);
                    psStud.setString(2, first);
                    psStud.setString(3, last);
                    psStud.setString(4, prog);
                    psStud.setInt(5, year);
                    psStud.setString(6, gender);
                    psStud.addBatch();
                }
                psStud.executeBatch();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //STUDENT METHODS
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

    public static void updateStudent(String id, String firstname, String lastname,
                                     String program, int year, String gender) {
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

    //PROGRAM METHODS
    public static void loadPrograms(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM program";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("college")
                });
            }
        } catch (Exception e) {
            System.out.println("ERROR IN loadPrograms()");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading programs:\n" + e.getMessage());
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

//COLLEGE METHODS
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
