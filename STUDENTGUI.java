import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGUI extends JFrame {

    private final String DB_URL = "jdbc:sqlite:ssis_database.db";
    private int studentPage = 0;
    private final int PAGE_SIZE = 15;

    DefaultTableModel studentModel, programModel, collegeModel;
    JTable studentTable, programTable, collegeTable;

    JTextField sid, sfname, slname, sprogram, syear, sgender, ssearch;
    JTextField pcode, pname, pcollege, psearch;
    JTextField ccode, cname, csearch;

    Color bgColor = new Color(30,30,30);
    Color panelColor = new Color(37,37,38);
    Color textColor = new Color(212,212,212);
    Color buttonColor = new Color(60,60,60);

    public StudentGUI() {
        initializeDatabase();
        applyTheming();

        setTitle("Simple Student Information System");
        setSize(1500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        // Header
        JLabel title = new JLabel("Simple Student Information System", SwingConstants.CENTER);
        title.setFont(new Font("Calibri", Font.BOLD, 35));
        title.setForeground(textColor);
        title.setOpaque(true);
        title.setBackground(panelColor);
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Students", buildStudentPanel());
        tabs.add("Programs", buildProgramPanel());
        tabs.add("Colleges", buildCollegePanel());
        add(tabs, BorderLayout.CENTER);

        refreshAllTables();
        setVisible(true);
    }

    private void applyTheming() {
        Font sans = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Label.font", sans);
        UIManager.put("Button.font", sans);
        UIManager.put("TextField.font", sans);
        UIManager.put("Table.font", sans);
        UIManager.put("Panel.background", panelColor);
        UIManager.put("Label.foreground", textColor);
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("CREATE TABLE IF NOT EXISTS colleges (code TEXT PRIMARY KEY, name TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS programs (code TEXT PRIMARY KEY, name TEXT, college_code TEXT, " +
                        "FOREIGN KEY(college_code) REFERENCES colleges(code) ON DELETE SET NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS student (id TEXT PRIMARY KEY, firstname TEXT, lastname TEXT, " +
                        "program_code TEXT, year TEXT, gender TEXT, " +
                        "FOREIGN KEY(program_code) REFERENCES programs(code) ON DELETE SET NULL)");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Init Error: " + e.getMessage());
        }
    }

    // --- PANEL BUILDERS ---

    JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        studentModel = new DefaultTableModel(new String[]{"ID","First","Last","Program","Year","Gender"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentModel);
        styleTable(studentTable);

        // Form Section
        JPanel form = new JPanel(new GridLayout(2, 6, 5, 5));
        form.setBackground(panelColor);
        sid = new JTextField(); sfname = new JTextField(); slname = new JTextField();
        sprogram = new JTextField(); syear = new JTextField(); sgender = new JTextField();
        addLabel(form, "ID"); addLabel(form, "First"); addLabel(form, "Last");
        addLabel(form, "Program"); addLabel(form, "Year"); addLabel(form, "Gender");
        form.add(sid); form.add(sfname); form.add(slname); form.add(sprogram); form.add(syear); form.add(sgender);
        styleTextFields(sid, sfname, slname, sprogram, syear, sgender);

        // Buttons Section
        JPanel buttons = new JPanel();
        buttons.setBackground(panelColor);
        JButton addBtn = createButton("Add");
        JButton upBtn = createButton("Update");
        JButton delBtn = createButton("Delete");
        ssearch = new JTextField(10);
        JButton searchBtn = createButton("Search");
        JButton prevBtn = createButton("Prev");
        JButton nextBtn = createButton("Next");

            buttons.add(prevBtn);
            buttons.add(nextBtn);

        addBtn.addActionListener(e -> addStudent());
        upBtn.addActionListener(e -> updateStudent());
        delBtn.addActionListener(e -> deleteStudent());
        searchBtn.addActionListener(e -> searchStudent(ssearch.getText()));
        prevBtn.addActionListener(e -> {
                if (studentPage > 0) {
                    studentPage--;
                    loadStudentPaginated();
                }
            });

            nextBtn.addActionListener(e -> {
                studentPage++;
                loadStudentPaginated();
            });


        buttons.add(addBtn); buttons.add(upBtn); buttons.add(delBtn);
        buttons.add(new JLabel(" Search: ")); buttons.add(ssearch); buttons.add(searchBtn);

            JButton genBtn = createButton("Generate 5000");
            genBtn.addActionListener(e -> generateStudents());

buttons.add(genBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    JPanel buildProgramPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        programModel = new DefaultTableModel(new String[]{"Code","Name","College"},0);
        programTable = new JTable(programModel);
        styleTable(programTable);

        JPanel form = new JPanel(new GridLayout(2,3, 5, 5));
        pcode = new JTextField(); pname = new JTextField(); pcollege = new JTextField();
        addLabel(form,"Code"); addLabel(form,"Name"); addLabel(form,"College");
        form.add(pcode); form.add(pname); form.add(pcollege);
        styleTextFields(pcode, pname, pcollege);

        JPanel buttons = new JPanel();
        JButton addBtn = createButton("Add");
        addBtn.addActionListener(e -> addProgram());
        buttons.add(addBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(programTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    JPanel buildCollegePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        collegeModel = new DefaultTableModel(new String[]{"Code","Name"}, 0);
        collegeTable = new JTable(collegeModel);
        styleTable(collegeTable);

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        ccode = new JTextField(); cname = new JTextField();
        addLabel(form, "Code"); addLabel(form, "Name");
        form.add(ccode); form.add(cname);
        styleTextFields(ccode, cname);

        JPanel buttons = new JPanel();
        JButton addBtn = createButton("Add");
        addBtn.addActionListener(e -> addCollege());
        buttons.add(addBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(collegeTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    // --- SQL OPERATIONS ---
    void addStudent() {
    String sql = "INSERT INTO student(id, firstname, lastname, course, year, gender) VALUES(?,?,?,?,?,?)";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, sid.getText());
        pstmt.setString(2, sfname.getText());
        pstmt.setString(3, slname.getText());
        pstmt.setString(4, sprogram.getText());
        pstmt.setInt(5, Integer.parseInt(syear.getText())); // better
        pstmt.setString(6, sgender.getText());

        pstmt.executeUpdate();

        loadStudentsPaginated(); // 👈 THIS is the pagination part
        clearStudentFields();

        JOptionPane.showMessageDialog(this, "Student Added!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

   void loadStudentPaginated() {

    String sql = "SELECT * FROM student LIMIT ? OFFSET ?";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, PAGE_SIZE);
        stmt.setInt(2, studentPage * PAGE_SIZE);

        ResultSet rs = stmt.executeQuery();

        studentModel.setRowCount(0);

        while (rs.next()) {
            studentModel.addRow(new Object[]{
                rs.getString("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("course"),
                rs.getInt("year"),
                rs.getString("gender")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    void addStudent() {
    String sql = "INSERT INTO student(id, firstname, lastname, course, year, gender) VALUES(?,?,?,?,?,?)";

    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, sid.getText());
        pstmt.setString(2, sfname.getText());
        pstmt.setString(3, slname.getText());
        pstmt.setString(4, sprogram.getText());
        pstmt.setInt(5, Integer.parseInt(syear.getText())); // better
        pstmt.setString(6, sgender.getText());

        pstmt.executeUpdate();

        loadStudentsPaginated(); // 👈 THIS is the pagination part
        clearStudentFields();

        JOptionPane.showMessageDialog(this, "Student Added!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}
    void updateStudent() {
        String sql = "UPDATE student SET firstname=?, lastname=?, program_code=?, year=?, gender=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sfname.getText());
            pstmt.setString(2, slname.getText());
            pstmt.setString(3, sprogram.getText());
            pstmt.setString(4, syear.getText());
            pstmt.setString(5, sgender.getText());
            pstmt.setString(6, sid.getText());
            pstmt.executeUpdate();
            loadStudentFromSQL();
            JOptionPane.showMessageDialog(this, "Updated!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void deleteStudent() {
        int r = studentTable.getSelectedRow();
        if (r == -1) return;
        String id = studentModel.getValueAt(r, 0).toString();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student WHERE id=?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            loadStudentFromSQL();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void searchStudent(String query) {
        String sql = "SELECT * FROM student WHERE id LIKE ? OR lastname LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%"+query+"%");
            pstmt.setString(2, "%"+query+"%");
            ResultSet rs = pstmt.executeQuery();
            studentModel.setRowCount(0);
            while (rs.next()) {
                studentModel.addRow(new Object[]{rs.getString("id"), rs.getString("firstname"), 
                    rs.getString("lastname"), rs.getString("program_code"), rs.getString("year"), rs.getString("gender")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }


    // --- PROGRAM & COLLEGE OPS ---

    void addProgram() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO programs VALUES(?,?,?)")) {
            pstmt.setString(1, pcode.getText());
            pstmt.setString(2, pname.getText());
            pstmt.setString(3, pcollege.getText());
            pstmt.executeUpdate();
            loadProgramsFromSQL();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void addCollege() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO colleges VALUES(?,?)")) {
            pstmt.setString(1, ccode.getText());
            pstmt.setString(2, cname.getText());
            pstmt.executeUpdate();
            loadCollegesFromSQL();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadProgramsFromSQL() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM programs")) {
            programModel.setRowCount(0);
            while (rs.next()) programModel.addRow(new Object[]{rs.getString("code"), rs.getString("name"), rs.getString("college_code")});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void loadCollegesFromSQL() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM colleges")) {
            collegeModel.setRowCount(0);
            while (rs.next()) collegeModel.addRow(new Object[]{rs.getString("code"), rs.getString("name")});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshAllTables() {
        loadStudentsFromSQL();
        loadProgramsFromSQL();
        loadCollegesFromSQL();
    }

    // --- HELPERS ---

    private void styleTable(JTable t) {
        t.setBackground(bgColor);
        t.setForeground(textColor);
        t.setRowHeight(25);
        t.getTableHeader().setBackground(buttonColor);
        t.getTableHeader().setForeground(Color.WHITE);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(buttonColor);
        b.setForeground(Color.WHITE);
        return b;
    }

    private void styleTextFields(JTextField... fields) {
        for(JTextField f : fields) {
            f.setBackground(new Color(50,50,50));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setForeground(textColor);
        p.add(l);
    }

    private void clearStudentFields() {
        sid.setText(""); sfname.setText(""); slname.setText("");
        sprogram.setText(""); syear.setText(""); sgender.setText("");
    }
}

    
