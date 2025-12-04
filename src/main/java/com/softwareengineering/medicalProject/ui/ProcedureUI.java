package com.softwareengineering.medicalProject.ui;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwareengineering.medicalProject.models.Procedure;

// ProcedureUI class provides the UI for managing Procedure records,
// it includes a table view and buttons for CRUD operations,
// interacts with a RESTful backend using the shared HttpHelper
public class ProcedureUI extends JFrame {

    private JTable procedureTable;
    private DefaultTableModel tableModel;
    private HttpHelper httpHelper;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "http://localhost:8080";

    // Constructor initializes the frame, and sets up the table model
    // Adds buttons for the CRUD operations
    public ProcedureUI() {
        super("Procedure Manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.httpHelper = new HttpHelper(this); 
        this.objectMapper = new ObjectMapper(); 

        tableModel = new DefaultTableModel(new String[]{"ID", "Procedure Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Long.class : String.class;
            }
        };

        procedureTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(procedureTable);

        // Action buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Procedure");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        // Action listeners for buttons
        refreshBtn.addActionListener(e -> loadProcedures());
        addBtn.addActionListener(e -> addProcedure());
        editBtn.addActionListener(e -> editProcedure());
        deleteBtn.addActionListener(e -> deleteProcedure());

        // Add to frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadProcedures();
        setVisible(true);
    }

    // Retrieves all procedure records from the backend, and populates the JTable
    private void loadProcedures() {
        tableModel.setRowCount(0);
        String json = httpHelper.httpGET(BASE_URL + "/procedures");
        if (json == null) {
            return;
        }
        try {
            List<Procedure> procedures = objectMapper.readValue(json, new TypeReference<List<Procedure>>() {});

            for (Procedure procedure : procedures) {
                tableModel.addRow(new Object[]{procedure.getId(), procedure.getProcedureName()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handles the "Add Procedure" action, promts the user for name, constructs the POST URL,
    // then sends the request, and refreshes the table
    private void addProcedure() {
        String name = JOptionPane.showInputDialog(this, "Enter procedure name:");
        if (name == null || name.isBlank()) {
            return;
        }
        String url = BASE_URL + "/addProcedure?procedureName=" + httpHelper.encodeParam(name);        
        httpHelper.httpPOST(url);
        loadProcedures();
    }

    // Handles the "Edit Procedure" action, gets selected row data, prompts user for name,
    // constructs the URL, sends PUT request, and then refreshes the table.
    private void editProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);

        String newName = JOptionPane.showInputDialog(this, "Edit name:", currentName);
        if (newName == null || newName.isBlank()) {
            return;
        }
        String url = BASE_URL + "/upsertProcedure?id=" + id + "&procedureName=" + httpHelper.encodeParam(newName);
        httpHelper.httpPUT(url);
        loadProcedures();
    }

    // Handles the "Delete Procedure" action, confirms the deletion of the selected procedure by ID
    // then sneds the delete request, and then refreshes the table
    private void deleteProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete procedure " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        String url = BASE_URL + "/deleteProcedure?id=" + id;
        httpHelper.httpDELETE(url);
        loadProcedures();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcedureUI::new);
    }
}