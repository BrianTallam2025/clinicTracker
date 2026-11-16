import javax.swing.*;// Brings in all visual UI Components(JFrame, JTable, all the Js)
import java.awt.*;// Manages the layout and styling tools.
import javax.swing.table.DefaultTableModel;//Inside a sub-package hence is independently imported.
import java.awt.event.ActionEvent;// Handles user interactions
import java.awt.event.ActionListener;//same
import java.util.List;//Enables us to read Tallam's list later.

public class ClinicForm extends JFrame{
    private JTextField name, phone, village, regDate, nextVisit, search;
    private  JComboBox<String> patientType, patientStatus;
    private JTable patientTable;
    private DefaultTableModel patientTableModel;

    //private PatientDAO patientDAO;

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
        //patientDAO = new PatientDAO();
        //loadTableData();
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
    
    private JPanel createTablePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patient Records"));

        //Defining the columns of the table to match with the DB
        String[] columms = {"ID", "Name", "Type", "Phone", "Village", "Registered", "Next Visit", "Status"};

        //DefaultTableModel holds the rows and  colums of data
        patientTableModel = new DefaultTableModel(columms, 0);
        patientTable = new JTable(patientTableModel);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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

        search = new JTextField(15);

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnDefaulters);
        panel.add(btnAll);
        panel.add(new JLabel("Search: "));
        panel.add(search);

        //Adding Event listeners that listen for user interaction
        //Add button
        btnAdd.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(ClinicForm.this, "Ready to save: " + name.getText());

            }
        });

        //Update button
        btnUpdate.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(ClinicForm.this, "Ready to update selected patient.");
            }
        });
        //Delete button
        btnDelete.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow == -1){
                    JOptionPane.showMessageDialog(ClinicForm.this, "Please select a patient first");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(
                    ClinicForm.this,
                    "Are you sure you want to delete this patient record?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION){
                    JOptionPane.showMessageDialog(ClinicForm.this, "Record Deleted!");
                }
            }
        });
        //Defaulters button
        btnDefaulters.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(ClinicForm.this, "Filtering Overdue Patients...");
            }
        });

        //Show all button
        btnAll.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(ClinicForm.this, "Showing all patients...");
            }
        });
        return panel;

    }


}