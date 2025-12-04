package com.softwareengineering.medicalProject.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softwareengineering.medicalProject.models.PatientHistory;

// PatientHistoryUI class provides the UI for managing Patient History records,
// it includes a table view and buttons for CRUD operations,
// interacts with a RESTful backend using the shared HttpHelper
public class PatientHistoryUI extends JFrame {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final ObjectMapper objectMapper;
    private HttpHelper httpHelper; 
    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private static final String[] COLUMN_NAMES = {
            "ID", "Patient ID", "Procedure ID", "Date/Time", "Doctor"
    };

    // Constructor initializes the frame, and sets up the table model
    // Adds buttons for the CRUD operations
    public PatientHistoryUI() {
        super("Patient History Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.httpHelper = new HttpHelper(this); 

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

        // Action buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add History");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action listeners for buttons
        refreshBtn.addActionListener(e -> loadPatientHistories());
        addBtn.addActionListener(e -> addPatientHistory());
        editBtn.addActionListener(e -> editPatientHistory());
        deleteBtn.addActionListener(e -> deletePatientHistory());

        // Add to frame
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
    
    // Retrieves all patient history records from the backend, deserializes the JSON,
    // and then populates the JTable accordingly
    private void loadPatientHistories() {
        tableModel.setRowCount(0);

        String json = httpHelper.httpGET(BASE_URL + "/patientHistoryAll");
        if (json == null) {
            return;
        }

        int rowCount = 0;
        try {
            List<PatientHistory> histories = objectMapper.readValue(json, new TypeReference<List<PatientHistory>>() {});

            for (PatientHistory history : histories) {
                String formattedDate;
                if (history.getDateOfProcedure() != null) {
                    formattedDate = history.getDateOfProcedure().format(DATE_FORMATTER);
                } else {
                    formattedDate = "";
                }
                  
                tableModel.addRow(new Object[]{
                        history.getId(),
                        history.getPatientIDKey(),
                        history.getProcedureIDKey(),
                        formattedDate,
                        history.getDoctor()
                });
                rowCount++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Displays a JOptionPane with the input fields for history details (Used by Add and Edit options)
    // Includes validation for numeric IDs and date/time format
    // Takes the initial values as parameters
    // Returns the array of Strings containing the collected field values,
    // or null if there's a cancelation or failure
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

    // Handles the "Add History" action, it prompts the user for data, then constructs the url,
    // then sends an HTTP POST request, and refreshes the table.
    private void addPatientHistory() {
        String[] fields = promptForHistoryFields("", "", LocalDateTime.now().format(DATE_FORMATTER), "");
        if (fields == null) {
            return;
        }

        String url = BASE_URL + "/addPatientHistory" +
                "?patientId=" + fields[0] +
                "&procedureId=" + fields[1] +
                "&dateOfProcedure=" + httpHelper.encodeParam(fields[2]) +
                "&doctor=" + httpHelper.encodeParam(fields[3]);

        httpHelper.httpPOST(url);
        loadPatientHistories();
    }

    // Handles the "Edit History" action, it gets the selected row data, prompts the user for updated data,
    // then constructs the url, sends an HTTP PUT request, and refreshes the table
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
        if (fields == null) {
            return;
        }

        String url = BASE_URL + "/upsertPatientHistory" +
                "?id=" + id +
                "&patientId=" + fields[0] +
                "&procedureId=" + fields[1] +
                "&dateOfProcedure=" + httpHelper.encodeParam(fields[2]) +
                "&doctor=" + httpHelper.encodeParam(fields[3]);

        httpHelper.httpPUT(url);
        loadPatientHistories();
    }

    // Handles the "Delete History" action: confirms deletion of the selected record by ID,
    // then sends an HTTP DELETE request, and refreshes the table.
    private void deletePatientHistory() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a history record first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete history record " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        httpHelper.httpDELETE(BASE_URL + "/deletePatientHistory?id=" + id);
        loadPatientHistories();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientHistoryUI::new);
    }
}