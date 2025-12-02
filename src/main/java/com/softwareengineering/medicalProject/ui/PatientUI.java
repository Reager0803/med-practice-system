package com.softwareengineering.medicalProject.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwareengineering.medicalProject.models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PatientUI extends JFrame {

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private static final String BASE_URL = "http://localhost:8080";

    private static final String[] COLUMN_NAMES = {
            "ID", "Last Name", "Middle Name", "First Name", "Address", "City", "State", "ZIP",
            "Phone", "Age", "Height (in)", "Weight (lbs)", "Insurance", "Doctor"
    };

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

        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Patient");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        refreshBtn.addActionListener(e -> loadPatients());
        addBtn.addActionListener(e -> addPatient());
        editBtn.addActionListener(e -> editPatient());
        deleteBtn.addActionListener(e -> deletePatient());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPatients();
        setVisible(true);
    }

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
            return response.toString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/GET error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String httpPOST(String urlStr) {
        return sendRequest("POST", urlStr);
    }

    private String httpPUT(String urlStr) {
        return sendRequest("PUT", urlStr);
    }

    private String httpDELETE(String urlStr) {
        return sendRequest("DELETE", urlStr);
    }

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
                    return "OK";
                } catch (Exception e) {
                    return "OK";
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
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/" + method + " error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void loadPatients() {
        tableModel.setRowCount(0);

        String json = httpGET(BASE_URL + "/patients");
        if (json == null) return;

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
                     return null;
                }
                String ageStr = ageField.getText().isBlank() ? "0" : ageField.getText();
                String heightStr = heightField.getText().isBlank() ? "0" : heightField.getText();
                String weightStr = weightField.getText().isBlank() ? "0" : weightField.getText();
                
                Long.parseLong(ageStr);
                Long.parseLong(heightStr);
                Long.parseLong(weightStr);

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
                return null;
            }
        }
        return null;
    }

    private String encodeParam(String param) {
        if (param == null) return "";
        try {
            return java.net.URLEncoder.encode(param, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return param.replace(" ", "%20");
        }
    }

    private void addPatient() {
        String[] fields = promptForPatientFields("", "", "", "", "", "", "", "", "", "", "", "", "");
        if (fields == null) return;

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
    }

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
        if (fields == null) return;

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
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete patient with ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        httpDELETE(BASE_URL + "/removePatient?id=" + id);
        loadPatients();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientUI::new);
    }
}