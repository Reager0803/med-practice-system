package com.softwareengineering.medicalProject.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softwareengineering.medicalProject.models.PatientHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientHistoryUI extends JFrame {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private static final String[] COLUMN_NAMES = {
            "ID", "Patient ID", "Procedure ID", "Date/Time", "Doctor"
    };

    public PatientHistoryUI() {
        super("Patient History Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 2) {
                    return Long.class; 
                }
                return String.class;
            }
        };

        historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add History");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        refreshBtn.addActionListener(e -> loadPatientHistories());
        addBtn.addActionListener(e -> addPatientHistory());
        editBtn.addActionListener(e -> editPatientHistory());
        deleteBtn.addActionListener(e -> deletePatientHistory());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPatientHistories();
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
                String errorResponse = readStream(con.getErrorStream());
                JOptionPane.showMessageDialog(this, "GET error (Status " + status + "): " + errorResponse);
                return null;
            }

            return readStream(con.getInputStream());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/GET error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String readStream(java.io.InputStream inputStream) throws java.io.IOException {
        if (inputStream == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private String sendRequest(String method, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.connect();

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                return "OK";
            } else {
                String errorResponse = readStream(con.getErrorStream());
                JOptionPane.showMessageDialog(this, method + " error (Status " + status + "): " + errorResponse);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Network/" + method + " error: " + e.getMessage());
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

    private String encodeParam(String param) {
        if (param == null) return "";
        try {
            return java.net.URLEncoder.encode(param, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return param.replace(" ", "%20");
        }
    }

    private void loadPatientHistories() {
        tableModel.setRowCount(0);

        String json = httpGET(BASE_URL + "/patientHistoryAll");
        if (json == null) return;

        try {
            List<PatientHistory> histories = objectMapper.readValue(json, new TypeReference<List<PatientHistory>>() {});

            for (PatientHistory history : histories) {
                String formattedDate = history.getDateOfProcedure() != null ? history.getDateOfProcedure().format(DATE_FORMATTER) : "";
                
                tableModel.addRow(new Object[]{
                        history.getId(),
                        history.getPatientIDKey(),
                        history.getProcedureIDKey(),
                        formattedDate,
                        history.getDoctor()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] promptForHistoryFields(String initialPatientID, String initialProcedureID, String initialDateTime, String initialDoctor) {
        JTextField patientIDField = new JTextField(initialPatientID);
        JTextField procedureIDField = new JTextField(initialProcedureID);
        JTextField dateTimeField = new JTextField(initialDateTime);
        JTextField doctorField = new JTextField(initialDoctor);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(300, 150)); 

        panel.add(new JLabel("Patient ID (Long):"));
        panel.add(patientIDField);
        panel.add(new JLabel("Procedure ID (Long):"));
        panel.add(procedureIDField);
        panel.add(new JLabel("Date/Time (YYYY-MM-DDTHH:MM:SS):"));
        panel.add(dateTimeField);
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorField);

        int result = JOptionPane.showConfirmDialog(this, panel, "History Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Long.parseLong(patientIDField.getText());
                Long.parseLong(procedureIDField.getText());
                
                if (!dateTimeField.getText().isBlank()) {
                     LocalDateTime.parse(dateTimeField.getText(), DATE_FORMATTER);
                }

                return new String[]{
                        patientIDField.getText(),
                        procedureIDField.getText(),
                        dateTimeField.getText(),
                        doctorField.getText()
                };
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Patient ID and Procedure ID must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } catch (java.time.format.DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Date/Time must be in the format YYYY-MM-DDTHH:MM:SS (e.g., 2023-12-01T14:30:00).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    private void addPatientHistory() {
        String[] fields = promptForHistoryFields("", "", LocalDateTime.now().format(DATE_FORMATTER), "");
        if (fields == null) return;

        String url = BASE_URL + "/addPatientHistory" +
                "?patientId=" + fields[0] +
                "&procedureId=" + fields[1] +
                "&dateOfProcedure=" + encodeParam(fields[2]) +
                "&doctor=" + encodeParam(fields[3]);

        httpPOST(url);
        loadPatientHistories();
    }

    private void editPatientHistory() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a history record first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        String patientId = String.valueOf(tableModel.getValueAt(row, 1));
        String procedureId = String.valueOf(tableModel.getValueAt(row, 2));
        String dateTime = (String) tableModel.getValueAt(row, 3);
        String doctor = (String) tableModel.getValueAt(row, 4);

        String[] fields = promptForHistoryFields(patientId, procedureId, dateTime, doctor);
        if (fields == null) return;

        String url = BASE_URL + "/upsertPatientHistory" +
                "?id=" + id +
                "&patientId=" + fields[0] +
                "&procedureId=" + fields[1] +
                "&dateOfProcedure=" + encodeParam(fields[2]) +
                "&doctor=" + encodeParam(fields[3]);

        httpPUT(url);
        loadPatientHistories();
    }

    private void deletePatientHistory() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a history record first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete history record " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        httpDELETE(BASE_URL + "/deletePatientHistory?id=" + id);
        loadPatientHistories();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientHistoryUI::new);
    }
}