package com.softwareengineering.medicalProject.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwareengineering.medicalProject.models.Patient;


// PatientUI class provides the UI for managing Patient records,
// it includes a table view and buttons for CRUD operations,
// interacts with a RESTful backend
public class PatientUI extends JFrame {

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private static final String BASE_URL = "http://localhost:8080";

    private static final String[] COLUMN_NAMES = {
            "ID", "Last Name", "Middle Name", "First Name", "Address", "City", "State", "ZIP",
            "Phone", "Age", "Height (in)", "Weight (lbs)", "Insurance", "Doctor"
    };

    // Constructor initializes the frame, and sets up the table model
    // Adds buttons for the CRUD operations
    public PatientUI() {
        super("Patient Manager");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 9 || columnIndex == 10 || columnIndex == 11) {
                    return Long.class; 
                }
                return String.class;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 

        patientTable.getColumnModel().getColumn(0).setPreferredWidth(50); 
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(100); 
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(150); 
        patientTable.getColumnModel().getColumn(8).setPreferredWidth(100); 
        patientTable.getColumnModel().getColumn(12).setPreferredWidth(100); 
        patientTable.getColumnModel().getColumn(13).setPreferredWidth(150); 


        JScrollPane scrollPane = new JScrollPane(patientTable);

        // Action buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Patient");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action listeners for buttons
        refreshBtn.addActionListener(e -> loadPatients());
        addBtn.addActionListener(e -> addPatient());
        editBtn.addActionListener(e -> editPatient());
        deleteBtn.addActionListener(e -> deletePatient());

        // Add to frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadPatients();
        setVisible(true);
        System.out.println("DEBUG: PatientUI initialized and displayed.");
    }

    // HTTP GET request to the url provided
    // returns the response body as a String, or null if failed
    private String httpGET(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                BufferedReader errReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errLine;
                while ((errLine = errReader.readLine()) != null) {
                    errorResponse.append(errLine);
                }
                errReader.close();
                JOptionPane.showMessageDialog(this, "GET error (Status " + status + "): " + errorResponse.toString());
                System.out.println("DEBUG: httpGET failed with status " + status + " for URL: " + urlStr);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            con.disconnect();
            System.out.println("DEBUG: httpGET success for " + urlStr + ". Response length: " + response.length());
            return response.toString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/GET error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: httpGET network error for URL: " + urlStr);
            return null;
        }
    }

    // Helper for POST
    private String httpPOST(String urlStr) {
        String result = sendRequest("POST", urlStr);
        System.out.println("DEBUG: httpPOST request completed for: " + urlStr);
        return result;
    }

    // Helper for PUT
    private String httpPUT(String urlStr) {
        String result = sendRequest("PUT", urlStr);
        System.out.println("DEBUG: httpPUT request completed for: " + urlStr);
        return result;
    }

    // Helper for DELETE
    private String httpDELETE(String urlStr) {
        String result = sendRequest("DELETE", urlStr);
        System.out.println("DEBUG: httpDELETE request completed for: " + urlStr);
        return result;
    }

    // Generic method to send non-GET requests
    // takes the method (POST, PUT, or DELETE), as a parameter
    // returns status (OK or error)
    private String sendRequest(String method, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.connect();

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
 
                    reader.close();
                    System.out.println("DEBUG: sendRequest (" + method + ") successful for URL: " + urlStr);
                    return "OK";
                } catch (Exception e) {
                    System.out.println("DEBUG: sendRequest (" + method + ") failed to read response stream for URL: " + urlStr);
                    return "error";
                }
            } else {
                BufferedReader errReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errLine;
 
                while ((errLine = errReader.readLine()) != null) {
                    errorResponse.append(errLine);
                }
 
                errReader.close();
                JOptionPane.showMessageDialog(this, method + " error (Status " + status + "): " + errorResponse.toString());
                System.out.println("DEBUG: sendRequest (" + method + ") failed with status " + status + " for URL: " + urlStr);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/" + method + " error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: sendRequest (" + method + ") network error for URL: " + urlStr);
            return null;
        }
    }

    // Retrieves all patient records from the backend, and populates the JTable
    private void loadPatients() {
        tableModel.setRowCount(0);

        String json = httpGET(BASE_URL + "/patients");
        if (json == null) {
            System.out.println("DEBUG: loadPatients failed to get JSON data.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Patient> patients = mapper.readValue(json, new TypeReference<List<Patient>>() {});

            for (Patient patient : patients) {
                tableModel.addRow(new Object[]{
                        patient.getId(),
                        patient.getLastName(),
                        patient.getMiddleName(),
                        patient.getFirstName(),
                        patient.getAddress(),
                        patient.getCity(),
                        patient.getState(),
                        patient.getZip(),
                        patient.getPhone(),
                        patient.getAge(),
                        patient.getHeight(),
                        patient.getWeight(),
                        patient.getInsurance(),
                        patient.getPrimaryCareDoctor()
                });
            }
            System.out.println("DEBUG: loadPatients finished. Total patients loaded: " + patients.size());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: loadPatients JSON parsing failed.");
        }
        System.out.println("DEBUG: loadPatients method ended.");
    }

    // Displays a JOptionPane with input fields for patient details (used by add/edit)
    // Takes initial values as parameters to fill the fileds
    // Returns an array of Strings containing the collected field values, or null if canceled or failure occurs.
    private String[] promptForPatientFields(String initialLastName, String initialMiddleName, String initialFirstName, String initialAddress,
                                            String initialCity, String initialState, String initialZip, String initialPhone,
                                            String initialAge, String initialHeight, String initialWeight, String initialInsurance,
                                            String initialDoctor) {
        
        JTextField lastNameField = new JTextField(initialLastName);
        JTextField middleNameField = new JTextField(initialMiddleName);
        JTextField firstNameField = new JTextField(initialFirstName);
        JTextField addressField = new JTextField(initialAddress);
        JTextField cityField = new JTextField(initialCity);
        JTextField stateField = new JTextField(initialState);
        JTextField zipField = new JTextField(initialZip);
        JTextField phoneField = new JTextField(initialPhone);
        JTextField ageField = new JTextField(initialAge);
        JTextField heightField = new JTextField(initialHeight);
        JTextField weightField = new JTextField(initialWeight);
        JTextField insuranceField = new JTextField(initialInsurance);
        JTextField doctorField = new JTextField(initialDoctor);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(350, 400)); 

        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Middle Name:"));
        panel.add(middleNameField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("--- Contact Info ---"));
        panel.add(new JLabel("")); 
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("State:"));
        panel.add(stateField);
        panel.add(new JLabel("ZIP:"));
        panel.add(zipField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("--- Medical Info ---"));
        panel.add(new JLabel("")); 
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Height (in):"));
        panel.add(heightField);
        panel.add(new JLabel("Weight (lbs):"));
        panel.add(weightField);
        panel.add(new JLabel("Insurance:"));
        panel.add(insuranceField);
        panel.add(new JLabel("Primary Doctor:"));
        panel.add(doctorField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Patient Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (lastNameField.getText().isBlank() || firstNameField.getText().isBlank()) {
                     JOptionPane.showMessageDialog(this, "Last Name and First Name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                     System.out.println("DEBUG: promptForPatientFields validation failed (Missing name).");
                     return null;
                }

                String ageStr;
                if (ageField.getText().isBlank()) {
                    ageStr = "0";
                } else {
                    ageStr = ageField.getText();
                }

                String heightStr;
                if (heightField.getText().isBlank()) {
                    heightStr = "0";
                } else {
                    heightStr = heightField.getText();
                }

                String weightStr;
                if (weightField.getText().isBlank()) {
                    weightStr = "0";
                } else {
                    weightStr = weightField.getText();
                }
                
                Long.parseLong(ageStr);
                Long.parseLong(heightStr);
                Long.parseLong(weightStr);

                System.out.println("DEBUG: promptForPatientFields finished. Returning valid fields.");

                return new String[]{
                        lastNameField.getText(),
                        middleNameField.getText(),
                        firstNameField.getText(),
                        addressField.getText(),
                        cityField.getText(),
                        stateField.getText(),
                        zipField.getText(),
                        phoneField.getText(),
                        ageStr,
                        heightStr,
                        weightStr,
                        insuranceField.getText(),
                        doctorField.getText()
                };
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Age, Height, and Weight must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("DEBUG: promptForPatientFields validation failed (NumberFormat).");
                return null;
            }
        }
        System.out.println("DEBUG: promptForPatientFields canceled or rejected.");
        return null;
    }

    // URL-encodes a string param to safely transmit it in an HTTP query
    private String encodeParam(String param) {
        if (param == null)
            return "";
        try {
            String encoded = java.net.URLEncoder.encode(param, "UTF-8");
            System.out.println("DEBUG: encodeParam original: '" + param + "', encoded: '" + encoded + "'");
            return java.net.URLEncoder.encode(param, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("DEBUG: encodeParam fallback used for: '" + param + "'");
            return param.replace(" ", "%20");
        }
    }

    // Handles the "Add Patient" action, promts the user for data, constructs the API URL,
    // then sends an HTTP POST request, and refreshes the table
    private void addPatient() {
        String[] fields = promptForPatientFields("", "", "", "", "", "", "", "", "", "", "", "", "");
        if (fields == null)
            return;

        String url = BASE_URL + "/addPatient" +
                "?lastName=" + encodeParam(fields[0]) +
                "&middleName=" + encodeParam(fields[1]) +
                "&firstName=" + encodeParam(fields[2]) +
                "&address=" + encodeParam(fields[3]) +
                "&city=" + encodeParam(fields[4]) +
                "&state=" + encodeParam(fields[5]) +
                "&zip=" + encodeParam(fields[6]) +
                "&phone=" + encodeParam(fields[7]) +
                "&age=" + fields[8] +
                "&height=" + fields[9] +
                "&weight=" + fields[10] +
                "&insurance=" + encodeParam(fields[11]) +
                "&doctor=" + encodeParam(fields[12]);

        httpPOST(url);
        loadPatients();
        System.out.println("DEBUG: addPatient completed and table refreshed.");
    }

    // Handles the "Edit Patient" action, gets selected row data, prompts user for updates,
    // constructs the API URL (including ID), sends an HTTP PUT request, and refreshes the table.
    private void editPatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        String lastName = (String) tableModel.getValueAt(row, 1);
        String middleName = (String) tableModel.getValueAt(row, 2);
        String firstName = (String) tableModel.getValueAt(row, 3);
        String address = (String) tableModel.getValueAt(row, 4);
        String city = (String) tableModel.getValueAt(row, 5);
        String state = (String) tableModel.getValueAt(row, 6);
        String zip = (String) tableModel.getValueAt(row, 7);
        String phone = (String) tableModel.getValueAt(row, 8);
        Long age = (Long) tableModel.getValueAt(row, 9);
        Long height = (Long) tableModel.getValueAt(row, 10);
        Long weight = (Long) tableModel.getValueAt(row, 11);
        String insurance = (String) tableModel.getValueAt(row, 12);
        String doctor = (String) tableModel.getValueAt(row, 13);


        String[] fields = promptForPatientFields(lastName, middleName, firstName, address, city, state, zip, phone,
                String.valueOf(age), String.valueOf(height), String.valueOf(weight), insurance, doctor);
        if (fields == null)
            return;

        String url = BASE_URL + "/upsertPatient" +
                "?id=" + id +
                "&lastName=" + encodeParam(fields[0]) +
                "&middleName=" + encodeParam(fields[1]) +
                "&firstName=" + encodeParam(fields[2]) +
                "&address=" + encodeParam(fields[3]) +
                "&city=" + encodeParam(fields[4]) +
                "&state=" + encodeParam(fields[5]) +
                "&zip=" + encodeParam(fields[6]) +
                "&phone=" + encodeParam(fields[7]) +
                "&age=" + fields[8] +
                "&height=" + fields[9] +
                "&weight=" + fields[10] +
                "&insurance=" + encodeParam(fields[11]) +
                "&doctor=" + encodeParam(fields[12]);

        httpPUT(url);
        loadPatients();
        System.out.println("DEBUG: editPatient completed and table refreshed.");
    }

    // Handles the Delete Patient action, confirms deletion, sends an HTTP DELETE request,
    // then refreshes the table
    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete patient with ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        httpDELETE(BASE_URL + "/removePatient?id=" + id);
        loadPatients();
        System.out.println("DEBUG: deletePatient completed and table refreshed.");
    }

    // Main entry point for the application
    public static void main(String[] args) {
        System.out.println("DEBUG: Starting PatientUI application...");
        SwingUtilities.invokeLater(PatientUI::new);
    }
}