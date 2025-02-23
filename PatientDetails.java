package patientdetails;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

class PatientDeatils extends JFrame {
    // Components of the form
    JLabel lPatientNumber, lFirstName, lLastName, lGender, lAge, lDOB, lBloodGroup, lAppointmentDate;
    JTextField tPatientNumber, tFirstName, tLastName, tAge;
    JComboBox<String> cbGender, cbBloodGroup;
    JButton bSave, bClear, bView, bViewAll;
    JSpinner dobSpinner, appointmentSpinner;
    Connection con;

    // Constructor to initialize the GUI components
    public PatientDeatils() {
        // Frame properties
        setTitle("Patient Details Form");

        // Labels
        lPatientNumber = new JLabel("Patient Number:");
        lPatientNumber.setBounds(50, 20, 120, 30);
        lFirstName = new JLabel("First Name:");
        lFirstName.setBounds(50, 60, 120, 30);
        lLastName = new JLabel("Last Name:");
        lLastName.setBounds(50, 100, 120, 30);
        lGender = new JLabel("Gender:");
        lGender.setBounds(50, 140, 120, 30);
        lAge = new JLabel("Age:");
        lAge.setBounds(50, 180, 120, 30);
        lDOB = new JLabel("Date of Birth:");
        lDOB.setBounds(50, 220, 120, 30);
        lBloodGroup = new JLabel("Blood Group:");
        lBloodGroup.setBounds(50, 260, 120, 30);
        lAppointmentDate = new JLabel("Appointment Date:");
        lAppointmentDate.setBounds(50, 300, 120, 30);

        // Text fields
        tPatientNumber = new JTextField();
        tPatientNumber.setBounds(180, 20, 200, 30);
        tFirstName = new JTextField();
        tFirstName.setBounds(180, 60, 200, 30);
        tLastName = new JTextField();
        tLastName.setBounds(180, 100, 200, 30);
        tAge = new JTextField();
        tAge.setBounds(180, 180, 200, 30);

        // ComboBoxes
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setBounds(180, 140, 200, 30);
        cbBloodGroup = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        cbBloodGroup.setBounds(180, 260, 200, 30);

        // Date of Birth Spinner
        dobSpinner = new JSpinner(new SpinnerDateModel());
        dobSpinner.setBounds(180, 220, 200, 30);
        JSpinner.DateEditor dobEditor = new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd");
        dobSpinner.setEditor(dobEditor);

        // Appointment Date Spinner
        appointmentSpinner = new JSpinner(new SpinnerDateModel());
        appointmentSpinner.setBounds(180, 300, 200, 30);
        JSpinner.DateEditor appointmentEditor = new JSpinner.DateEditor(appointmentSpinner, "yyyy-MM-dd");
        appointmentSpinner.setEditor(appointmentEditor);

        // Buttons
        bSave = new JButton("Save");
        bSave.setBounds(50, 350, 100, 30);
        bClear = new JButton("Clear");
        bClear.setBounds(160, 350, 100, 30);
        bView = new JButton("View Details");
        bView.setBounds(270, 350, 120, 30);
        bViewAll = new JButton("View All");
        bViewAll.setBounds(400, 350, 100, 30);

        // Adding components to the frame
        add(lPatientNumber); add(lFirstName); add(lLastName);
        add(lGender); add(lAge); add(lDOB);
        add(lBloodGroup); add(lAppointmentDate);
        add(tPatientNumber); add(tFirstName); add(tLastName);
        add(tAge); add(cbGender); add(cbBloodGroup);
        add(dobSpinner); add(appointmentSpinner);
        add(bSave); add(bClear); add(bView); add(bViewAll);

        setSize(550, 450);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Action Listeners for Buttons
        bSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePatient();
            }
        });

        bClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        bView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPatient();
            }
        });

        bViewAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAllPatients();
            }
        });

        // Database Connection
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/Patients","java","java");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to save patient details
    public void savePatient() {
        try {
            String query = "INSERT INTO patients (patient_number, first_name, last_name, gender, age, dob, blood_group, appointment_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, tPatientNumber.getText());
            pst.setString(2, tFirstName.getText());
            pst.setString(3, tLastName.getText());
            pst.setString(4, (String) cbGender.getSelectedItem());
            pst.setInt(5, Integer.parseInt(tAge.getText()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pst.setString(6, dateFormat.format(dobSpinner.getValue()));
            pst.setString(7, (String) cbBloodGroup.getSelectedItem());
            pst.setString(8, dateFormat.format(appointmentSpinner.getValue()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Patient details saved successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to clear all fields
    public void clearFields() {
        tPatientNumber.setText("");
        tFirstName.setText("");
        tLastName.setText("");
        tAge.setText("");
        cbGender.setSelectedIndex(0);
        cbBloodGroup.setSelectedIndex(0);
        dobSpinner.setValue(new java.util.Date());
        appointmentSpinner.setValue(new java.util.Date());
    }

    // Method to view patient details
    public void viewPatient() {
        try {
            String query = "SELECT * FROM patients WHERE patient_number = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, tPatientNumber.getText());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Create a new JFrame to display patient details
                JFrame detailsFrame = new JFrame("Patient Details");
                detailsFrame.setSize(400, 300);
                detailsFrame.setLayout(new GridLayout(8, 2, 10, 10));
                
                // Create labels to display patient details
                detailsFrame.add(new JLabel("Patient Number:"));
                detailsFrame.add(new JLabel(rs.getString("patient_number")));
                detailsFrame.add(new JLabel("First Name:"));
                detailsFrame.add(new JLabel(rs.getString("first_name")));
                detailsFrame.add(new JLabel("Last Name:"));
                detailsFrame.add(new JLabel(rs.getString("last_name")));
                detailsFrame.add(new JLabel("Gender:"));
                detailsFrame.add(new JLabel(rs.getString("gender")));
                detailsFrame.add(new JLabel("Age:"));
                detailsFrame.add(new JLabel(String.valueOf(rs.getInt("age"))));
                detailsFrame.add(new JLabel("Date of Birth:"));
                detailsFrame.add(new JLabel(rs.getString("dob")));
                detailsFrame.add(new JLabel("Blood Group:"));
                detailsFrame.add(new JLabel(rs.getString("blood_group")));
                detailsFrame.add(new JLabel("Appointment Date:"));
                detailsFrame.add(new JLabel(rs.getString("appointment_date")));
                
                detailsFrame.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(this, "No patient found with the provided number.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to view all patients
    public void viewAllPatients() {
        try {
            String query = "SELECT * FROM patients";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // Create a new JFrame to display all patients
            JFrame allPatientsFrame = new JFrame("All Patients");
            allPatientsFrame.setSize(600, 400);
            
            // Create table model and table
            DefaultTableModel model = new DefaultTableModel();
            JTable table = new JTable(model);
            
            // Define columns for the table
            model.addColumn("Patient Number");
            model.addColumn("First Name");
            model.addColumn("Last Name");
            model.addColumn("Gender");
            model.addColumn("Age");
            model.addColumn("Date of Birth");
            model.addColumn("Blood Group");
            model.addColumn("Appointment Date");
            
            // Populate table with data from the database
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("patient_number"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("gender"),
                    rs.getInt("age"),
                    rs.getString("dob"),
                    rs.getString("blood_group"),
                    rs.getString("appointment_date")
                });
            }
            
            // Add table to JScrollPane and add to frame
            allPatientsFrame.add(new JScrollPane(table));
            allPatientsFrame.setVisible(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PatientDeatils();
    }

}

