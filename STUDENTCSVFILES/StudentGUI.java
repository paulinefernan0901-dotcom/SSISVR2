import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGUI extends JFrame {

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
        
        Font googleSans = new Font("Google Sans", Font.PLAIN, 15);
        UIManager.put("Label.font", googleSans);
        UIManager.put("Button.font", googleSans);
        UIManager.put("TextField.font", googleSans);
        UIManager.put("Table.font", googleSans);
        UIManager.put("TableHeader.font", new Font("Google Sans", Font.BOLD, 16));
        UIManager.put("TabbedPane.font", googleSans);
        UIManager.put("OptionPane.messageFont", googleSans);
        UIManager.put("OptionPane.buttonFont", googleSans);
        UIManager.put("OptionPane.background", panelColor);
        UIManager.put("Panel.background", panelColor);
        UIManager.put("OptionPane.messageForeground", textColor);
        UIManager.put("ComboBox.background", buttonColor);
        UIManager.put("ComboBox.foreground", textColor);
        UIManager.put("ComboBox.font", googleSans);
        UIManager.put("ComboBox.selectionBackground", new Color(75, 75, 75));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("List.background", panelColor);
        UIManager.put("List.foreground", textColor);
        UIManager.put("List.font", googleSans); 

        try {
            UIManager.put("TabbedPane.selected", panelColor);
            UIManager.put("TabbedPane.contentAreaColor", bgColor);
            UIManager.put("TabbedPane.background", panelColor);
            UIManager.put("TabbedPane.foreground", textColor);
            UIManager.put("ScrollPane.background", bgColor);
            UIManager.put("Viewport.background", bgColor);
            UIManager.put("OptionPane.background", panelColor);
            UIManager.put("Panel.background", panelColor);
            UIManager.put("OptionPane.messageForeground", textColor);
            UIManager.put("Button.background", buttonColor);
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception e) { e.printStackTrace(); }

        setTitle("Simple Student Information System");
        setSize(1500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        JLabel title = new JLabel("Simple Student Information System", SwingConstants.CENTER);
        title.setFont(new Font("Calibri", Font.BOLD, 35));
        title.setForeground(textColor);
        title.setOpaque(true);
        title.setBackground(panelColor);
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(panelColor);
        tabs.setForeground(textColor);

        tabs.add("Students", buildStudentPanel());
        tabs.add("Programs", buildProgramPanel());
        tabs.add("Colleges", buildCollegePanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

   JPanel buildStudentPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(bgColor);

    studentModel = new DefaultTableModel(new String[]{"ID","First","Last","Program","Year","Gender"}, 0);
    studentTable = new JTable(studentModel);
    styleTable(studentTable);
    DatabaseHelper.generateStudents(studentModel);
    studentTable.getSelectionModel().addListSelectionListener(e -> {
        int r = studentTable.getSelectedRow();
        if (r != -1) {
            sid.setText(studentModel.getValueAt(r, 0).toString());
            sfname.setText(studentModel.getValueAt(r, 1).toString());
            slname.setText(studentModel.getValueAt(r, 2).toString());
            sprogram.setText(studentModel.getValueAt(r, 3).toString());
            syear.setText(studentModel.getValueAt(r, 4).toString());
            sgender.setText(studentModel.getValueAt(r, 5).toString());
        }
    });

   
    JPanel form = new JPanel(new GridLayout(2, 6));
    form.setBackground(panelColor);
    sid = new JTextField(); sfname = new JTextField(); slname = new JTextField();
    sprogram = new JTextField(); syear = new JTextField(); sgender = new JTextField();

    addLabel(form, "ID"); addLabel(form, "First"); addLabel(form, "Last");
    addLabel(form, "Program"); addLabel(form, "Year"); addLabel(form, "Gender");
    form.add(sid); form.add(sfname); form.add(slname); form.add(sprogram); form.add(syear); form.add(sgender);
    styleTextFields(sid, sfname, slname, sprogram, syear, sgender);

    JPanel buttons = new JPanel();
    buttons.setBackground(panelColor);
    
    JButton addBtn = createButton("Add");
    JButton upBtn = createButton("Update");
    JButton delBtn = createButton("Delete");
    JButton sortBtn = createButton("Sort");
    JButton searchBtn = createButton("Search");
    JButton prev = createButton("Previous");
    JButton next = createButton("Next");

 
    ssearch = new JTextField(10); 
    styleTextFields(ssearch);
    
    String[] searchOptions = {"ID Number", "First Name", "Last Name"};
    JComboBox<String> searchFieldPicker = new JComboBox<>(searchOptions);
    searchFieldPicker.setFont(new Font("Google Sans", Font.PLAIN, 15));
    searchFieldPicker.setBackground(buttonColor);
    searchFieldPicker.setForeground(Color.WHITE);

    addBtn.addActionListener(e -> addStudent());
    upBtn.addActionListener(e -> updateStudent());
    delBtn.addActionListener(e -> deleteStudent());
    
    
    searchBtn.addActionListener(e -> {
        String query = ssearch.getText();
        int selectedIndex = searchFieldPicker.getSelectedIndex();
        searchStudent(query, selectedIndex);
    });

    sortBtn.addActionListener(e -> {
        String[] options = {"ID", "First Name", "Last Name", "Program", "Year", "Gender"};
        String choice = (String) JOptionPane.showInputDialog(this, "Sort students by:", "Sort Options", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice != null) {
            int colIndex = 0;
            switch (choice) {
                case "ID": colIndex = 0; break;
                case "First Name": colIndex = 1; break;
                case "Last Name": colIndex = 2; break;
                case "Program": colIndex = 3; break;
                case "Year": colIndex = 4; break;
                case "Gender": colIndex = 5; break;
            }
           DatabaseHelper.generateStudents(studentModel);
            studentTable.getRowSorter().toggleSortOrder(colIndex);
        }
    });

    
    buttons.add(addBtn); buttons.add(upBtn); buttons.add(delBtn); buttons.add(sortBtn);
    buttons.add(new JLabel(" Search By:"));
    buttons.add(searchFieldPicker);
    buttons.add(ssearch);
    buttons.add(searchBtn);

    panel.add(form, BorderLayout.NORTH);
    panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);
    return panel;
}

    JPanel buildProgramPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(bgColor);
    programModel = new DefaultTableModel(new String[]{"Code","Name","College"},0);
    programTable = new JTable(programModel);
    styleTable(programTable);

    DatabaseHelper.generatePrograms(programModel);
    
    
    programTable.getSelectionModel().addListSelectionListener(e -> {
        int r = programTable.getSelectedRow();
        if (r != -1) {
            pcode.setText(programModel.getValueAt(r, 0).toString());
            pname.setText(programModel.getValueAt(r, 1).toString());
            pcollege.setText(programModel.getValueAt(r, 2).toString());
        }
    });

    JPanel form = new JPanel(new GridLayout(2,3));
    form.setBackground(panelColor);
    pcode = new JTextField(); pname = new JTextField(); pcollege = new JTextField();
    addLabel(form,"Code"); addLabel(form,"Name"); addLabel(form,"College");
    form.add(pcode); form.add(pname); form.add(pcollege);
    styleTextFields(pcode,pname,pcollege);

    JPanel buttons = new JPanel();
    buttons.setBackground(panelColor);
    JButton add = createButton("Add");
    JButton update = createButton("Update");
    JButton del = createButton("Delete");
    JButton sort = createButton("Sort");
    JButton searchBtn = createButton("Search");

    psearch = new JTextField(10);
    styleTextFields(psearch);

    String[] pSearchOptions = {"Code", "Name", "College"};
    JComboBox<String> pSearchPicker = new JComboBox<>(pSearchOptions);
    pSearchPicker.setFont(new Font("Google Sans", Font.PLAIN, 15));
    pSearchPicker.setBackground(buttonColor);
    pSearchPicker.setForeground(Color.WHITE);

    add.addActionListener(e -> addProgram());
    update.addActionListener(e -> updateProgram());
    del.addActionListener(e -> deleteProgram());
    sort.addActionListener(e -> { 
        DatabaseHelper.generatePrograms(programModel);
    });

    searchBtn.addActionListener(e -> {
        String query = psearch.getText().toLowerCase();
        int fieldIndex = pSearchPicker.getSelectedIndex(); 
        boolean found = false;

        for (int i = 0; i < programModel.getRowCount(); i++) {
            if (programModel.getValueAt(i, fieldIndex).toString().toLowerCase().contains(query)) {
                programTable.setRowSelectionInterval(i, i);
                programTable.scrollRectToVisible(programTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        if (!found) JOptionPane.showMessageDialog(this, "No program found.");
    });

    buttons.add(add); buttons.add(update); buttons.add(del);
    buttons.add(sort); 
    buttons.add(new JLabel(" Search By:"));
    buttons.add(pSearchPicker);
    buttons.add(psearch); 
    buttons.add(searchBtn);

    panel.add(form, BorderLayout.NORTH);
    panel.add(new JScrollPane(programTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);
    return panel;
}

    JPanel buildCollegePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(bgColor);
    collegeModel = new DefaultTableModel(new String[]{"Code","Name"}, 0);
    collegeTable = new JTable(collegeModel);
    styleTable(collegeTable);
    DatabaseHelper.loadColleges(collegeModel);
    JPanel form = new JPanel(new GridLayout(2, 2));
    form.setBackground(panelColor);
    ccode = new JTextField(); cname = new JTextField();
    addLabel(form, "Code"); addLabel(form, "Name");
    form.add(ccode); form.add(cname);
    styleTextFields(ccode, cname);

    JPanel buttons = new JPanel();
    buttons.setBackground(panelColor);
    
    csearch = new JTextField(10);
    styleTextFields(csearch);

    String[] cSearchOptions = {"Code", "Name"};
    JComboBox<String> cSearchPicker = new JComboBox<>(cSearchOptions);
    cSearchPicker.setFont(new Font("Google Sans", Font.PLAIN, 15));
    cSearchPicker.setBackground(buttonColor);
    cSearchPicker.setForeground(Color.WHITE);

    JButton addBtn = createButton("Add");
    JButton updateBtn = createButton("Update");
    JButton delBtn = createButton("Delete");
    JButton sortBtn = createButton("Sort");
    JButton searchBtn = createButton("Search");

    addBtn.addActionListener(e -> addCollege());
    updateBtn.addActionListener(e -> updateCollege());
    delBtn.addActionListener(e -> deleteCollege());
    sortBtn.addActionListener(e -> { 
        DatabaseHelper.loadColleges(collegeModel);
    });

    searchBtn.addActionListener(e -> {
        String query = csearch.getText().toLowerCase();
        int fieldIndex = cSearchPicker.getSelectedIndex(); 
        boolean found = false;

        for (int i = 0; i < collegeModel.getRowCount(); i++) {
            if (collegeModel.getValueAt(i, fieldIndex).toString().toLowerCase().contains(query)) {
                collegeTable.setRowSelectionInterval(i, i);
                collegeTable.scrollRectToVisible(collegeTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        if (!found) JOptionPane.showMessageDialog(this, "No college found.");
    });

    buttons.add(addBtn); buttons.add(updateBtn); buttons.add(delBtn);
    buttons.add(sortBtn);
    buttons.add(new JLabel(" Search By:"));
    buttons.add(cSearchPicker);
    buttons.add(csearch);
    buttons.add(searchBtn);

    panel.add(form, BorderLayout.NORTH);
    panel.add(new JScrollPane(collegeTable), BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);
    return panel;
}

    JButton createButton(String text){
        JButton b = new JButton(text);
        b.setBackground(buttonColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    void styleTextFields(JTextField... fields){
        for(JTextField f: fields){
            f.setBackground(new Color(50,50,50));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }
    }

    void addLabel(JPanel panel,String text){
        JLabel l = new JLabel(text);
        l.setForeground(textColor);
        panel.add(l);
    }

    void styleTable(JTable table) {
        table.setBackground(bgColor);
        table.setForeground(textColor);
        table.setGridColor(new Color(60, 60, 60));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(75, 75, 75));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(buttonColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if (table.getParent() instanceof JViewport) { table.getParent().setBackground(bgColor); }
    }

 
    void addStudent() {
        
                if (!sid.getText().matches("\\d{4}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "ID must be in YYYY-NNNN format (e.g., 2024-1234)");
            return;
                }
            if (sid.getText().isEmpty() || sfname.getText().isEmpty() || slname.getText().isEmpty()
                    || sprogram.getText().isEmpty() || syear.getText().isEmpty() || sgender.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
                return; 
            }

            DatabaseHelper.addStudent(
                sid.getText(),
                sfname.getText(),
                slname.getText(),
                sprogram.getText(),
                Integer.parseInt(syear.getText()),
                sgender.getText()
        );

            DatabaseHelper.loadStudents(studentModel);

        
            sid.setText(""); sfname.setText(""); slname.setText("");
            sprogram.setText(""); syear.setText(""); sgender.setText("");

            JOptionPane.showMessageDialog(this, "Student added successfully!");
        }
    

    void updateStudent() {

            int r = studentTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student to update.");
                return;
            }

            // get original ID from selected row (important!)
            int modelRow = studentTable.convertRowIndexToModel(r);
            String originalId = studentModel.getValueAt(modelRow, 0).toString();

            // update DB
            DatabaseHelper.updateStudent(
                    originalId,
                    sfname.getText(),
                    slname.getText(),
                    sprogram.getText(),
                    Integer.parseInt(syear.getText()),
                    sgender.getText()
            );

            // refresh table
            DatabaseHelper.loadStudents(studentModel);

            JOptionPane.showMessageDialog(this, "Student updated successfully!");
        }

    void searchStudent(String query, int fieldIndex) {

            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.");
                return;
            }

            String field = "";

            switch (fieldIndex) {
                case 0: field = "id"; break;
                case 1: field = "firstname"; break;
                case 2: field = "lastname"; break;
            }

            DatabaseHelper.searchStudents(studentModel, query, field);
        }

    void deleteStudent() {

            int r = studentTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.");
                return;
            }

            int modelRow = studentTable.convertRowIndexToModel(r);
            String id = studentModel.getValueAt(modelRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this student?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                DatabaseHelper.deleteStudent(id);

                DatabaseHelper.loadStudents(studentModel);

                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
            }
        }


        void addProgram() {

            if (pcode.getText().isEmpty() || pname.getText().isEmpty() || pcollege.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            DatabaseHelper.addProgram(
                    pcode.getText(),
                    pname.getText(),
                    pcollege.getText()
            );

            DatabaseHelper.loadPrograms(programModel);

            JOptionPane.showMessageDialog(this, "Program added successfully!");

            pcode.setText("");
            pname.setText("");
            pcollege.setText("");
        }

         void updateProgram() {

            int r = programTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Please select a program to update.");
                return;
            }

            int modelRow = programTable.convertRowIndexToModel(r);
            String originalCode = programModel.getValueAt(modelRow, 0).toString();

            DatabaseHelper.updateProgram(
                    originalCode,
                    pname.getText(),
                    pcollege.getText()
            );

            DatabaseHelper.loadPrograms(programModel);

            JOptionPane.showMessageDialog(this, "Program updated successfully!");
        }

            void deleteProgram() {

            int r = programTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Please select a program to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this program?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                int modelRow = programTable.convertRowIndexToModel(r);
                String code = programModel.getValueAt(modelRow, 0).toString();

                DatabaseHelper.deleteProgram(code);

                DatabaseHelper.loadPrograms(programModel);

                JOptionPane.showMessageDialog(this, "Program deleted successfully!");
            }
        }
    void searchProgram(String query, int fieldIndex) {
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.");
            return;
        }
        String field = "";
        switch (fieldIndex) {
            case 0: field = "code"; break;
            case 1: field = "name"; break;
            case 2: field = "college"; break;
        }
        DatabaseHelper.searchPrograms(programModel, query, field);
    }

    void addCollege() {
            if (ccode.getText().isEmpty() || cname.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            DatabaseHelper.addCollege(
                    ccode.getText(),
                    cname.getText()
            );
            DatabaseHelper.loadColleges(collegeModel);
            JOptionPane.showMessageDialog(this, "College added successfully!");
            ccode.setText("");
            cname.setText("");
        }

    void updateCollege() {
        int r = collegeTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Please select a college to update.");
            return;
        }
        int modelRow = collegeTable.convertRowIndexToModel(r);
        String originalCode = collegeModel.getValueAt(modelRow, 0).toString();
        DatabaseHelper.updateCollege(
                originalCode,
                cname.getText()
        );
        DatabaseHelper.loadColleges(collegeModel);
        JOptionPane.showMessageDialog(this, "College updated successfully!");
    }

    void deleteCollege() {

        int r = collegeTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Please select a college to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this college?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = collegeTable.convertRowIndexToModel(r);
            String code = collegeModel.getValueAt(modelRow, 0).toString();
            DatabaseHelper.deleteCollege(code);
            DatabaseHelper.loadColleges(collegeModel);
            JOptionPane.showMessageDialog(this, "College deleted successfully!");
        }
}
    void searchCollege(String query, int fieldIndex) {

            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.");
                return;
            }
            String field = "";
            switch (fieldIndex) {
                case 0: field = "code"; break;
                case 1: field = "name"; break;
            }
            DatabaseHelper.searchColleges(collegeModel, query, field);
        }
}
