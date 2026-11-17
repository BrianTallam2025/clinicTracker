import javax.swing.*;// Brings in all visual UI Components(JFrame, JTable, all the Js)
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;// Manages the layout and styling tools.
import javax.swing.table.DefaultTableModel;//Inside a sub-package hence is independently imported.
import java.awt.event.ActionEvent;// Handles user interactions
import java.awt.event.ActionListener;//same
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;//Enables us to read Tallam's list later.
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



public class ClinicForm extends JFrame{
    private JTextField name, phone, village, regDate, nextVisit, search;
    private  JComboBox<String> patientType, patientStatus;
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private PatientDAO patientDAO = new PatientDAO();
    

    public ClinicForm(){
        // Setting up window settings, size etc.
        setTitle("Maternal & Child Health Clinic Tracker");
        setSize(1000,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Layout setup
        setLayout(new BorderLayout(10, 10));

        //Building the UI sections
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        //loads all patients from MySQL
        loadPatients("", false);
    }

private void loadPatients(String searchText, boolean defaultersOnly) {
        try {
            List<Patient> patients = patientDAO.findPatients(searchText, defaultersOnly);
            patientTableModel.setRowCount(0);

            for (Patient p : patients) {
                patientTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getFullName(),
                    p.getPatientType(),
                    p.getPhone(),
                    p.getVillage(),
                    p.getDateRegistered(),
                    p.getNextVisit(),
                    p.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearForm() {
        name.setText("");
        phone.setText("");
        village.setText("");
        regDate.setText("");
        nextVisit.setText("");
        patientType.setSelectedIndex(0);
        patientStatus.setSelectedIndex(0);
        patientTable.clearSelection();
    }

    private JPanel createFormPanel(){
        JPanel panel = new JPanel(new GridLayout(10, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Patient Information"));
        panel.setPreferredSize(new Dimension(320,0));

        name = new JTextField();
        phone = new JTextField();
        village = new JTextField();
        regDate = new JTextField();
        nextVisit = new JTextField();
        search = new JTextField();

        String[] types = {"Pregnant Mother", "Child"};
        patientType = new JComboBox<>(types);

        String[] statuses = {"Active", "Defaulter", "Completed"};
        patientStatus = new JComboBox<>(statuses);

        //Adding labels
        panel.add(new JLabel("Full Name:"));
        panel.add(name);

        panel.add(new JLabel("Patient Type: "));
        panel.add(patientType);

        panel.add(new JLabel("Phone Number: "));
        panel.add(phone);

        panel.add(new JLabel("Village: "));
        panel.add(village);

        panel.add(new JLabel("Registration Date: "));
        panel.add(regDate);

        panel.add(new JLabel("Next Visit Date:"));
        panel.add(nextVisit);

        panel.add(new JLabel("Status: "));
        panel.add(patientStatus);

        return panel;
    }
    private boolean isInputValid() {
        // 1. Grab the text from your JTextFields and remove extra spaces
        String nameText = name.getText().trim();
        String phoneText = phone.getText().trim();
        String villageText = village.getText().trim();
        String regDateText = regDate.getText().trim();
        String nextVisitText = nextVisit.getText().trim();

        // CHECK 1: Are any fields blank?
        if (nameText.isEmpty() || phoneText.isEmpty() || villageText.isEmpty() || regDateText.isEmpty() || nextVisitText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before saving.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // CHECK 2: Strict Kenyan phone number check (e.g., 0712345678, 0112345678, or +254712345678)
        if (!phoneText.matches("^(\\+254|0)[17][0-9]{8}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Kenyan phone number (e.g., 0712345678 or +254712345678).", "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // CHECK 3 & 4: Are the dates formatted properly (YYYY-MM-DD) AND in logical order?
        try {
            LocalDate parsedRegDate = LocalDate.parse(regDateText);
            LocalDate parsedNextVisit = LocalDate.parse(nextVisitText);

            if (parsedNextVisit.isBefore(parsedRegDate)) {
                JOptionPane.showMessageDialog(this, "Next Visit Date cannot be earlier than the Registration Date.", "Date Logic Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Dates must follow the YYYY-MM-DD format (e.g., 2026-11-17).", "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // All checks passed!
        return true;
    }
    
    private JPanel createTablePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patient Records"));

        //Defining the columns of the table to match with the DB
        String[] columns = {"ID", "Full Name", "Patient Type", "Phone", "Village", "Date Registered", "Next Visit", "Status"};

        //DefaultTableModel holds the rows and  colums of data
        patientTableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(patientTableModel);

        patientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    name.setText(patientTableModel.getValueAt(row, 1).toString());
                    patientType.setSelectedItem(patientTableModel.getValueAt(row, 2).toString());
                    phone.setText(patientTableModel.getValueAt(row, 3).toString());
                    village.setText(patientTableModel.getValueAt(row, 4).toString());
                    regDate.setText(patientTableModel.getValueAt(row, 5).toString());
                    nextVisit.setText(patientTableModel.getValueAt(row, 6).toString());
                    patientStatus.setSelectedItem(patientTableModel.getValueAt(row, 7).toString());
                }
            }
        });

        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        return panel;
    }
    private JPanel createControlPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        panel.setBorder(BorderFactory.createEtchedBorder());

        JButton btnAdd = new JButton("Add Patient");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnDefaulters = new JButton("Show Defaulters");
        JButton btnAll = new JButton("Show All");

        // LIVE SEARCH BAR
        search = new JTextField(15);
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadPatients(search.getText(), false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadPatients(search.getText(), false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadPatients(search.getText(), false);
            }
        });

        // ADD BUTTON
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isInputValid()) {
                    return;
                }

                try {
                    LocalDate reg = LocalDate.parse(regDate.getText().trim());
                    LocalDate next = LocalDate.parse(nextVisit.getText().trim());

                    Patient newPatient = new Patient(
                        name.getText().trim(),
                        patientType.getSelectedItem().toString(),
                        phone.getText().trim(),
                        village.getText().trim(),
                        reg,
                        reg,
                        next,
                        patientStatus.getSelectedItem().toString()
                    );

                    patientDAO.addPatient(newPatient);
                    loadPatients("", false);
                    clearForm();
                    JOptionPane.showMessageDialog(ClinicForm.this, "Patient saved successfully!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(ClinicForm.this, "Error saving patient: " + ex.getMessage());
                }
            }
        });

        // UPDATE BUTTON
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ClinicForm.this, "Please click a patient row in the table first.");
                    return;
                }
                if (!isInputValid()) {
                    return;
                }

                try {
                    int id = (int) patientTableModel.getValueAt(selectedRow, 0);
                    LocalDate reg = LocalDate.parse(regDate.getText().trim());
                    LocalDate next = LocalDate.parse(nextVisit.getText().trim());

                    Patient updated = new Patient(
                        id,
                        name.getText().trim(),
                        patientType.getSelectedItem().toString(),
                        phone.getText().trim(),
                        village.getText().trim(),
                        reg,
                        LocalDate.now(),
                        next,
                        patientStatus.getSelectedItem().toString()
                    );

                    patientDAO.updatePatient(updated);
                    loadPatients("", false);
                    clearForm();
                    JOptionPane.showMessageDialog(ClinicForm.this, "Patient updated!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(ClinicForm.this, "Error updating patient: " + ex.getMessage());
                }
            }
        });

        // DELETE BUTTON
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(ClinicForm.this, "Please select a patient row to delete.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(ClinicForm.this, "Delete this patient record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = (int) patientTableModel.getValueAt(selectedRow, 0);
                        patientDAO.deletePatient(id);
                        loadPatients("", false);
                        clearForm();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(ClinicForm.this, "Error deleting patient: " + ex.getMessage());
                    }
                }
            }
        });

        // SHOW DEFAULTERS BUTTON
        btnDefaulters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPatients("", true);
            }
        });

        // SHOW ALL BUTTON
        btnAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search.setText("");
                loadPatients("", false);
            }
        });
        // SEARCH, SHOW DEFAULTERS & SHOW ALL BUTTONS
        btnDefaulters.addActionListener(e -> loadPatients("", true));
        btnAll.addActionListener(e -> {
            search.setText("");
            loadPatients("", false);
        });
    
        return panel;
        
}
}