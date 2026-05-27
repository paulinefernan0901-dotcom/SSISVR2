import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGUI extends JFrame {

    DefaultTableModel studentModel, programModel, collegeModel;
    JTable studentTable, programTable, collegeTable;

    JTextField sid, sfname, slname, sprogram, syear, sgender, ssearch;
    JTextField pcode, pname, pcollege, psearch;
    JTextField ccode, cname, csearch;

    Color bgColor    = new Color(30, 30, 30);
    Color panelColor = new Color(37, 37, 38);
    Color textColor  = new Color(212, 212, 212);
    Color buttonColor = new Color(60, 60, 60);//to make sure that the dark mode/black theme is
                                              //cosistent

    //CREATING PAGINATION FOR STUDENT PANEL
    ArrayList<Object[]> allStudentRows = new ArrayList<>();
    int studentCurrentPage = 0;
    final int PAGE_SIZE = 15;//max number of students shown per page
    JLabel studentPageLabel;//1/500

    //PAGINATION FOR PROGRAM PANEL
    ArrayList<Object[]> allProgramRows = new ArrayList<>();
    int programCurrentPage = 0;
    JLabel programPageLabel;

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
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();//separates the 3 panels into tabs
        tabs.setBackground(panelColor);
        tabs.setForeground(textColor);

        tabs.add("Students", buildStudentPanel());
        tabs.add("Programs", buildProgramPanel());
        tabs.add("Colleges", buildCollegePanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    //STUDENT PANEL
    JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        studentModel = new DefaultTableModel(
                new String[]{"ID", "First", "Last", "Program", "Year", "Gender"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            //not allow user to edit on the table directly, must use buttons to edit
        };
        studentTable = new JTable(studentModel);
        styleTable(studentTable);

        loadAllStudentsIntoList();//Loads ALL student records from database into ArrayList
        showStudentPage(0);

        studentTable.getSelectionModel().addListSelectionListener(e -> {//if click a row, shows the data in textfield
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

        //FORM OF THE STUDENT PANEL
        JPanel form = new JPanel(new GridLayout(2, 6));//2 rows(textfield and label, 6 columns
        form.setBackground(panelColor);
        sid = new JTextField(); sfname = new JTextField(); slname = new JTextField();
        sprogram = new JTextField(); syear = new JTextField(); sgender = new JTextField();
        addLabel(form, "ID"); addLabel(form, "First"); addLabel(form, "Last");
        addLabel(form, "Program"); addLabel(form, "Year"); addLabel(form, "Gender");
        form.add(sid); form.add(sfname); form.add(slname);
        form.add(sprogram); form.add(syear); form.add(sgender);
        styleTextFields(sid, sfname, slname, sprogram, syear, sgender);

        //BUTTONS
        JPanel buttons = new JPanel();
        buttons.setBackground(panelColor);

        JButton addBtn    = createButton("Add");
        JButton upBtn     = createButton("Update");
        JButton delBtn    = createButton("Delete");
        JButton sortBtn   = createButton("Sort");
        JButton searchBtn = createButton("Search");
        JButton prevBtn   = createButton("◀ Prev");
        JButton nextBtn   = createButton("Next ▶");

        studentPageLabel = new JLabel("Page 1");
        studentPageLabel.setForeground(textColor);

        ssearch = new JTextField(10);
        styleTextFields(ssearch);

        String[] searchOptions = {"ID Number", "First Name", "Last Name"};
        JComboBox<String> searchFieldPicker = new JComboBox<>(searchOptions);
        styleCombo(searchFieldPicker);

        //BUTTON ACTIONS
        addBtn.addActionListener(e -> addStudent());
        upBtn.addActionListener(e -> updateStudent());
        delBtn.addActionListener(e -> deleteStudent());

        sortBtn.addActionListener(e -> {
            String[] options = {"ID", "First Name", "Last Name", "Program", "Year", "Gender"};
            String choice = (String) JOptionPane.showInputDialog(this,
                    "Sort students by:", "Sort Options",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice != null) {
                int col = 0;
                switch (choice) {
                    case "ID":         col = 0; break;
                    case "First Name": col = 1; break;
                    case "Last Name":  col = 2; break;
                    case "Program":    col = 3; break;
                    case "Year":       col = 4; break;
                    case "Gender":     col = 5; break;
                }
                DefaultTableModel tmp = new DefaultTableModel(
                        new String[]{"ID","First","Last","Program","Year","Gender"}, 0);
                DatabaseHelper.loadStudents(tmp);
                allStudentRows.clear();
                for (int i = 0; i < tmp.getRowCount(); i++) {
                    Object[] row = new Object[tmp.getColumnCount()];
                    for (int c = 0; c < tmp.getColumnCount(); c++) row[c] = tmp.getValueAt(i, c);
                    allStudentRows.add(row);
                }
                final int sortCol = col;
                allStudentRows.sort((a, b) -> a[sortCol].toString().compareToIgnoreCase(b[sortCol].toString()));
                showStudentPage(0);
            }
        });

        searchBtn.addActionListener(e -> {
            String query = ssearch.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.");
                return;
            }
            String[] fieldMap = {"id", "firstname", "lastname"};
            String field = fieldMap[searchFieldPicker.getSelectedIndex()];

            DefaultTableModel tmp = new DefaultTableModel(
                    new String[]{"ID","First","Last","Program","Year","Gender"}, 0);
            DatabaseHelper.searchStudents(tmp, query, field);
            allStudentRows.clear();
            for (int i = 0; i < tmp.getRowCount(); i++) {
                Object[] row = new Object[tmp.getColumnCount()];
                for (int c = 0; c < tmp.getColumnCount(); c++) row[c] = tmp.getValueAt(i, c);
                allStudentRows.add(row);
            }
            showStudentPage(0);
        });

        prevBtn.addActionListener(e -> {//previous button for pagination
            if (studentCurrentPage > 0) showStudentPage(studentCurrentPage - 1);
        });
        nextBtn.addActionListener(e -> {//next button for pagination
            int lastPage = (allStudentRows.size() - 1) / PAGE_SIZE;
            if (studentCurrentPage < lastPage) showStudentPage(studentCurrentPage + 1);
        });

        buttons.add(addBtn); buttons.add(upBtn); buttons.add(delBtn); buttons.add(sortBtn);
        buttons.add(new JLabel(" Search By:"));
        buttons.add(searchFieldPicker);
        buttons.add(ssearch);
        buttons.add(searchBtn);
        buttons.add(prevBtn);
        buttons.add(studentPageLabel);
        buttons.add(nextBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    //pull all student data from database into ArrayList
    void loadAllStudentsIntoList() {
        DefaultTableModel tmp = new DefaultTableModel(
                new String[]{"ID","First","Last","Program","Year","Gender"}, 0);
        DatabaseHelper.loadStudents(tmp);
        allStudentRows.clear();
        for (int i = 0; i < tmp.getRowCount(); i++) {
            Object[] row = new Object[tmp.getColumnCount()];
            for (int c = 0; c < tmp.getColumnCount(); c++) row[c] = tmp.getValueAt(i, c);
            allStudentRows.add(row);
        }
    }

    //REPOPULATE THE VISIBLE TABLE WITH THE ROWS FOR THE REQUESTED PAGE
    void showStudentPage(int page) {//display the students according to their pages
        studentCurrentPage = page;
        studentModel.setRowCount(0);

        int start = page * PAGE_SIZE;
        int end   = Math.min(start + PAGE_SIZE, allStudentRows.size());

        for (int i = start; i < end; i++) studentModel.addRow(allStudentRows.get(i));

        int totalPages = allStudentRows.isEmpty() ? 1 : (int) Math.ceil((double) allStudentRows.size() / PAGE_SIZE);
        if (studentPageLabel != null)
            studentPageLabel.setText("Page " + (page + 1) + " / " + totalPages
                    + "  (" + allStudentRows.size() + " records)");
    }

    //PROGRAM PANEL
    JPanel buildProgramPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        programModel = new DefaultTableModel(new String[]{"Code", "Name", "College"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        programTable = new JTable(programModel);
        styleTable(programTable);

        loadAllProgramsIntoList();
        showProgramPage(0);

        programTable.getSelectionModel().addListSelectionListener(e -> {
            int r = programTable.getSelectedRow();
            if (r != -1) {
                pcode.setText(programModel.getValueAt(r, 0).toString());
                pname.setText(programModel.getValueAt(r, 1).toString());
                pcollege.setText(programModel.getValueAt(r, 2).toString());
            }
        });

        //program panel form
        JPanel form = new JPanel(new GridLayout(2, 3));
        form.setBackground(panelColor);
        pcode = new JTextField(); pname = new JTextField(); pcollege = new JTextField();
        addLabel(form, "Code"); addLabel(form, "Name"); addLabel(form, "College");
        form.add(pcode); form.add(pname); form.add(pcollege);
        styleTextFields(pcode, pname, pcollege);

        //BUTTONS
        JPanel buttons = new JPanel();
        buttons.setBackground(panelColor);

        JButton add       = createButton("Add");
        JButton update    = createButton("Update");
        JButton del       = createButton("Delete");
        JButton sort      = createButton("Sort");
        JButton searchBtn = createButton("Search");
        JButton prevBtn   = createButton("◀ Prev");
        JButton nextBtn   = createButton("Next ▶");

        programPageLabel = new JLabel("Page 1");
        programPageLabel.setForeground(textColor);

        psearch = new JTextField(10);
        styleTextFields(psearch);

        String[] pSearchOptions = {"Code", "Name", "College"};
        JComboBox<String> pSearchPicker = new JComboBox<>(pSearchOptions);
        styleCombo(pSearchPicker);

        //BUTTON ACTIONS
        add.addActionListener(e -> addProgram());
        update.addActionListener(e -> updateProgram());
        del.addActionListener(e -> deleteProgram());

        sort.addActionListener(e -> {
            loadAllProgramsIntoList();
            showProgramPage(0);
        });

        searchBtn.addActionListener(e -> {
            String query = psearch.getText().trim().toLowerCase();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.");
                return;
            }
            int fi = pSearchPicker.getSelectedIndex();
            allProgramRows.clear();
            DefaultTableModel tmp = new DefaultTableModel(new String[]{"Code","Name","College"}, 0);
            DatabaseHelper.loadPrograms(tmp);
            for (int i = 0; i < tmp.getRowCount(); i++) {
                if (tmp.getValueAt(i, fi).toString().toLowerCase().contains(query)) {
                    Object[] row = {tmp.getValueAt(i,0), tmp.getValueAt(i,1), tmp.getValueAt(i,2)};
                    allProgramRows.add(row);
                }
            }
            if (allProgramRows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No program found.");
                loadAllProgramsIntoList();
            }
            showProgramPage(0);
        });

        prevBtn.addActionListener(e -> {
            if (programCurrentPage > 0) showProgramPage(programCurrentPage - 1);
        });
        nextBtn.addActionListener(e -> {
            int lastPage = (allProgramRows.size() - 1) / PAGE_SIZE;
            if (programCurrentPage < lastPage) showProgramPage(programCurrentPage + 1);
        });

        buttons.add(add); buttons.add(update); buttons.add(del); buttons.add(sort);
        buttons.add(new JLabel(" Search By:"));
        buttons.add(pSearchPicker);
        buttons.add(psearch);
        buttons.add(searchBtn);
        buttons.add(prevBtn);
        buttons.add(programPageLabel);
        buttons.add(nextBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(programTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    void loadAllProgramsIntoList() {
        DefaultTableModel tmp = new DefaultTableModel(new String[]{"Code","Name","College"}, 0);
        DatabaseHelper.loadPrograms(tmp);
        allProgramRows.clear();
        for (int i = 0; i < tmp.getRowCount(); i++) {
            Object[] row = {tmp.getValueAt(i,0), tmp.getValueAt(i,1), tmp.getValueAt(i,2)};
            allProgramRows.add(row);
        }
    }

    void showProgramPage(int page) {
        programCurrentPage = page;
        programModel.setRowCount(0);

        int start = page * PAGE_SIZE;
        int end   = Math.min(start + PAGE_SIZE, allProgramRows.size());

        for (int i = start; i < end; i++) programModel.addRow(allProgramRows.get(i));

        int totalPages = allProgramRows.isEmpty() ? 1 : (int) Math.ceil((double) allProgramRows.size() / PAGE_SIZE);
        if (programPageLabel != null)
            programPageLabel.setText("Page " + (page + 1) + " / " + totalPages
                    + "  (" + allProgramRows.size() + " records)");
    }

    //COLLEGE PANEL

    //COLLEGE PANEL FORM
    JPanel buildCollegePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        collegeModel = new DefaultTableModel(new String[]{"Code", "Name"}, 0);
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
        styleCombo(cSearchPicker);
        //BUTTONS
        JButton addBtn    = createButton("Add");
        JButton updateBtn = createButton("Update");
        JButton delBtn    = createButton("Delete");
        JButton sortBtn   = createButton("Sort");
        JButton searchBtn = createButton("Search");

        //BUTTON ACTIONS
        addBtn.addActionListener(e -> addCollege());
        updateBtn.addActionListener(e -> updateCollege());
        delBtn.addActionListener(e -> deleteCollege());
        sortBtn.addActionListener(e -> DatabaseHelper.loadColleges(collegeModel));

        searchBtn.addActionListener(e -> {
            String query = csearch.getText().toLowerCase();
            int fi = cSearchPicker.getSelectedIndex();
            boolean found = false;
            for (int i = 0; i < collegeModel.getRowCount(); i++) {
                if (collegeModel.getValueAt(i, fi).toString().toLowerCase().contains(query)) {
                    collegeTable.setRowSelectionInterval(i, i);
                    collegeTable.scrollRectToVisible(collegeTable.getCellRect(i, 0, true));
                    found = true;
                    break;
                }
            }
            if (!found) JOptionPane.showMessageDialog(this, "No college found.");
        });

        buttons.add(addBtn); buttons.add(updateBtn); buttons.add(delBtn); buttons.add(sortBtn);
        buttons.add(new JLabel(" Search By:"));
        buttons.add(cSearchPicker);
        buttons.add(csearch);
        buttons.add(searchBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(collegeTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    //CRUD FOR STUDENT PANEL
    void addStudent() {//add button action for student panel
        if (!sid.getText().matches("\\d{4}-\\d{4}")) {//id format check
            JOptionPane.showMessageDialog(this, "ID must be in YYYY-NNNN format (e.g., 2024-1234)");
            return;
        }
        if (sid.getText().isEmpty() || sfname.getText().isEmpty() || slname.getText().isEmpty()
                || sprogram.getText().isEmpty() || syear.getText().isEmpty() || sgender.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DatabaseHelper.addStudent(sid.getText(), sfname.getText(), slname.getText(),
                sprogram.getText(), Integer.parseInt(syear.getText()), sgender.getText());
        //
        DefaultTableModel tmp = new DefaultTableModel(new String[]{"ID","First","Last","Program","Year","Gender"}, 0);
        DatabaseHelper.loadStudents(tmp);
        allStudentRows.clear();
        for (int i = 0; i < tmp.getRowCount(); i++) {
            Object[] row = new Object[tmp.getColumnCount()];
            for (int c = 0; c < tmp.getColumnCount(); c++) row[c] = tmp.getValueAt(i, c);
            allStudentRows.add(row);
        }
        int lastPage = allStudentRows.isEmpty() ? 0 : (allStudentRows.size() - 1) / PAGE_SIZE;
        showStudentPage(lastPage);

        sid.setText(""); sfname.setText(""); slname.setText("");
        sprogram.setText(""); syear.setText(""); sgender.setText("");
        JOptionPane.showMessageDialog(this, "Student added successfully!");
    }

    void updateStudent() {//update button action for student panel
        int r = studentTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a student to update."); return; }
        String originalId = studentModel.getValueAt(r, 0).toString();
        DatabaseHelper.updateStudent(originalId, sfname.getText(), slname.getText(),
                sprogram.getText(), Integer.parseInt(syear.getText()), sgender.getText());

        for (Object[] row : allStudentRows) {
            if (row[0].toString().equals(originalId)) {
                row[1] = sfname.getText(); row[2] = slname.getText();
                row[3] = sprogram.getText(); row[4] = Integer.parseInt(syear.getText());
                row[5] = sgender.getText();
                break;
            }
        }
        showStudentPage(studentCurrentPage);
        JOptionPane.showMessageDialog(this, "Student updated successfully!");
    }

    void deleteStudent() {//delete button action for student panel
        int r = studentTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a student to delete."); return; }
        String id = studentModel.getValueAt(r, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseHelper.deleteStudent(id);
            allStudentRows.removeIf(row -> row[0].toString().equals(id));
            int newPage = Math.max(0, Math.min(studentCurrentPage,
                    allStudentRows.isEmpty() ? 0 : (allStudentRows.size() - 1) / PAGE_SIZE));
            showStudentPage(newPage);
            JOptionPane.showMessageDialog(this, "Student deleted successfully!");
        }
    }

    //CRUD FOR PROGRAM PANEL
    void addProgram() {//add button action for program panel
        if (pcode.getText().isEmpty() || pname.getText().isEmpty() || pcollege.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields."); return;
        }
        DatabaseHelper.addProgram(pcode.getText(), pname.getText(), pcollege.getText());

        Object[] newRow = {pcode.getText(), pname.getText(), pcollege.getText()};
        allProgramRows.add(newRow);
        int lastPage = (allProgramRows.size() - 1) / PAGE_SIZE;
        showProgramPage(lastPage);

        pcode.setText(""); pname.setText(""); pcollege.setText("");
        JOptionPane.showMessageDialog(this, "Program added successfully!");
    }

    void updateProgram() {//update button action for program panel
        int r = programTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a program to update."); return; }
        String originalCode = programModel.getValueAt(r, 0).toString();
        DatabaseHelper.updateProgram(originalCode, pname.getText(), pcollege.getText());

        for (Object[] row : allProgramRows) {
            if (row[0].toString().equals(originalCode)) {
                row[1] = pname.getText(); row[2] = pcollege.getText(); break;
            }
        }
        showProgramPage(programCurrentPage);
        JOptionPane.showMessageDialog(this, "Program updated successfully!");
    }

    void deleteProgram() {//delete button action for program panel
        int r = programTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a program to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this program?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String code = programModel.getValueAt(r, 0).toString();
            DatabaseHelper.deleteProgram(code);
            allProgramRows.removeIf(row -> row[0].toString().equals(code));
            int newPage = Math.max(0, Math.min(programCurrentPage,
                    allProgramRows.isEmpty() ? 0 : (allProgramRows.size() - 1) / PAGE_SIZE));
            showProgramPage(newPage);
            JOptionPane.showMessageDialog(this, "Program deleted successfully!");
        }
    }

    //CRUD FOR COLLEGE PANEL
    void addCollege() {//add button action for college panel
        if (ccode.getText().isEmpty() || cname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields."); return;
        }
        DatabaseHelper.addCollege(ccode.getText(), cname.getText());
        DatabaseHelper.loadColleges(collegeModel);
        JOptionPane.showMessageDialog(this, "College added successfully!");
        ccode.setText(""); cname.setText("");
    }

    void updateCollege() {//update button action for college panel
        int r = collegeTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a college to update."); return; }
        int modelRow = collegeTable.convertRowIndexToModel(r);
        String originalCode = collegeModel.getValueAt(modelRow, 0).toString();
        DatabaseHelper.updateCollege(originalCode, cname.getText());
        DatabaseHelper.loadColleges(collegeModel);
        JOptionPane.showMessageDialog(this, "College updated successfully!");
    }

    void deleteCollege() {//delete button action for college panel
        int r = collegeTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Please select a college to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this college?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = collegeTable.convertRowIndexToModel(r);
            String code = collegeModel.getValueAt(modelRow, 0).toString();
            DatabaseHelper.deleteCollege(code);
            DatabaseHelper.loadColleges(collegeModel);
            JOptionPane.showMessageDialog(this, "College deleted successfully!");
        }
    }

    void searchCollege(String query, int fieldIndex) {//search button action for college panel
        if (query.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a search term."); return; }
        String field = (fieldIndex == 0) ? "code" : "name";
        DatabaseHelper.searchColleges(collegeModel, query, field);
    }

    //REUSED SO I WONT HAVE TO TYPE IT 3 TIMES
    JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(buttonColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    void styleTextFields(JTextField... fields) {//APPLY THEME COLORS TO TEXT FIELDS
        for (JTextField f : fields) {
            f.setBackground(new Color(50, 50, 50));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }
    }

    void styleCombo(JComboBox<?> cb) {//APPLY THEME COLORS TO A COMBO BOX
        cb.setFont(new Font("Google Sans", Font.PLAIN, 15));
        cb.setBackground(buttonColor);
        cb.setForeground(Color.WHITE);
    }

    void addLabel(JPanel panel, String text) {//UTILITY METHOD TO ADD A LABEL WITH THEME COLORS
        JLabel l = new JLabel(text);
        l.setForeground(textColor);
        panel.add(l);
    }

    void styleTable(JTable table) {//APPLY THEME COLORS TO A TABLE
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
        if (table.getParent() instanceof JViewport) table.getParent().setBackground(bgColor);
    }
}
