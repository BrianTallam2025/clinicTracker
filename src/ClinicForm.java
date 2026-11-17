import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ClinicForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField name = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField village = new JTextField();
    private final JTextField regDate = new JTextField();
    private final JTextField lastVisit = new JTextField();
    private final JTextField nextVisit = new JTextField();
    private final JTextField search = new JTextField(15);
    private final JComboBox<String> patientType =
            new JComboBox<>(new String[]{"Pregnant Mother", "Child"});
    private final JComboBox<String> patientStatus =
            new JComboBox<>(new String[]{"Active", "Defaulter", "Completed"});
    private JTable patientTable;
    private DefaultTableModel patientTableModel;
    private final PatientDAO patientDAO = new PatientDAO();
    private Integer selectedPatientId;
    private boolean showingDefaulters;

    public ClinicForm() {
        setTitle("Maternal & Child Health Clinic Tracker");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Patient Information"));
        panel.setPreferredSize(new Dimension(310, 0));
        addFormRow(panel, "Full Name:", name);
        addFormRow(panel, "Patient Type:", patientType);
        addFormRow(panel, "Phone Number:", phone);
        addFormRow(panel, "Village:", village);
        addFormRow(panel, "Registration Date (YYYY-MM-DD):", regDate);
        addFormRow(panel, "Last Visit (YYYY-MM-DD):", lastVisit);
        addFormRow(panel, "Next Visit (YYYY-MM-DD):", nextVisit);
        addFormRow(panel, "Status:", patientStatus);

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(event -> clearForm());
        panel.add(new JLabel());
        panel.add(clearButton);
        return panel;
    }

    private void addFormRow(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patient Records"));
        String[] columns = {"ID", "Name", "Type", "Phone", "Village",
                "Registered", "Last Visit", "Next Visit", "Status"};
        patientTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(patientTableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setAutoCreateRowSorter(true);
        patientTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedPatient();
            }
        });
        patientTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                if (!selected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String status = String.valueOf(patientTableModel.getValueAt(modelRow, 8));
                    LocalDate visitDate = LocalDate.parse(
                            String.valueOf(patientTableModel.getValueAt(modelRow, 7)));
                    long daysUntilVisit = ChronoUnit.DAYS.between(LocalDate.now(), visitDate);
                    if (!"Completed".equals(status) && visitDate.isBefore(LocalDate.now())) {
                        cell.setBackground(new Color(255, 205, 210));
                    } else if (!"Completed".equals(status) && daysUntilVisit <= 7) {
                        cell.setBackground(new Color(255, 224, 178));
                    } else {
                        cell.setBackground(table.getBackground());
                    }
                    cell.setForeground(table.getForeground());
                }
                return cell;
            }
        });
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEtchedBorder());
        JButton addButton = new JButton("Add Patient");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton defaultersButton = new JButton("Show Defaulters");
        JButton allButton = new JButton("Show All");
        JButton searchButton = new JButton("Search");

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(defaultersButton);
        panel.add(allButton);
        panel.add(new JLabel("Search:"));
        panel.add(search);
        panel.add(searchButton);

        addButton.addActionListener(event -> addPatient());
        updateButton.addActionListener(event -> updatePatient());
        deleteButton.addActionListener(event -> deletePatient());
        defaultersButton.addActionListener(event -> {
            showingDefaulters = true;
            refreshTable();
        });
        allButton.addActionListener(event -> {
            showingDefaulters = false;
            search.setText("");
            refreshTable();
        });
        searchButton.addActionListener(event -> refreshTable());
        search.addActionListener(event -> refreshTable());
        return panel;
    }

    private void addPatient() {
        try {
            patientDAO.addPatient(readPatient());
            clearForm();
            refreshTable();
            showMessage("Patient added successfully.");
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void updatePatient() {
        if (selectedPatientId == null) {
            showMessage("Select a patient record to update.");
            return;
        }
        try {
            Patient editedPatient = readPatient();
            Patient patient = new Patient(selectedPatientId, editedPatient.getFullName(),
                    editedPatient.getPatientType(), editedPatient.getPhone(), editedPatient.getVillage(),
                    editedPatient.getDateRegistered(), editedPatient.getLastVisit(),
                    editedPatient.getNextVisit(), editedPatient.getStatus());
            patientDAO.updatePatient(patient);
            clearForm();
            refreshTable();
            showMessage("Patient updated successfully.");
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void deletePatient() {
        if (selectedPatientId == null) {
            showMessage("Select a patient record to delete.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Delete the selected patient record?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            patientDAO.deletePatient(selectedPatientId);
            clearForm();
            refreshTable();
            showMessage("Patient deleted successfully.");
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private Patient readPatient() {
        String fullName = name.getText().trim();
        String patientPhone = phone.getText().trim();
        String patientVillage = village.getText().trim();
        if (fullName.isEmpty() || patientPhone.isEmpty() || patientVillage.isEmpty()) {
            throw new IllegalArgumentException("Name, phone number, and village are required.");
        }
        try {
            return new Patient(fullName, (String) patientType.getSelectedItem(), patientPhone, patientVillage,
                    LocalDate.parse(regDate.getText().trim()), LocalDate.parse(lastVisit.getText().trim()),
                    LocalDate.parse(nextVisit.getText().trim()), (String) patientStatus.getSelectedItem());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Enter all dates using YYYY-MM-DD.");
        }
    }

    private void refreshTable() {
        try {
            List<Patient> patients = patientDAO.findPatients(search.getText(), showingDefaulters);
            patientTableModel.setRowCount(0);
            for (Patient patient : patients) {
                patientTableModel.addRow(new Object[]{
                        patient.getId(), patient.getFullName(), patient.getPatientType(), patient.getPhone(),
                        patient.getVillage(), patient.getDateRegistered(), patient.getLastVisit(),
                        patient.getNextVisit(), patient.getStatus()
                });
            }
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private void clearForm() {
        name.setText("");
        phone.setText("");
        village.setText("");
        regDate.setText("");
        lastVisit.setText("");
        nextVisit.setText("");
        patientType.setSelectedIndex(0);
        patientStatus.setSelectedIndex(0);
        selectedPatientId = null;
        patientTable.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showDatabaseError(SQLException exception) {
        JOptionPane.showMessageDialog(this,
                "Could not access the clinic database: " + exception.getMessage()
                        + "\nCheck MySQL, the JDBC driver, and database settings.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void loadSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedPatientId = null;
            return;
        }
        int row = patientTable.convertRowIndexToModel(selectedRow);
        selectedPatientId = ((Number) patientTableModel.getValueAt(row, 0)).intValue();
        name.setText(String.valueOf(patientTableModel.getValueAt(row, 1)));
        patientType.setSelectedItem(patientTableModel.getValueAt(row, 2));
        phone.setText(String.valueOf(patientTableModel.getValueAt(row, 3)));
        village.setText(String.valueOf(patientTableModel.getValueAt(row, 4)));
        regDate.setText(String.valueOf(patientTableModel.getValueAt(row, 5)));
        lastVisit.setText(String.valueOf(patientTableModel.getValueAt(row, 6)));
        nextVisit.setText(String.valueOf(patientTableModel.getValueAt(row, 7)));
        patientStatus.setSelectedItem(patientTableModel.getValueAt(row, 8));
    }
}