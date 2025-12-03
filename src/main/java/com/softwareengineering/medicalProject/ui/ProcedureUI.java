package com.softwareengineering.medicalProject.ui;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


// ProcedureUI class provides the UI for managing Procedure records,
// it includes a table view and buttons for CRUD operations,
// interacts with a RESTful backend
public class ProcedureUI extends JFrame {

    private JTable procedureTable;
    private DefaultTableModel tableModel;
    private static final String BASE_URL = "http://localhost:8080";

    // Constructor initializes the frame, and sets up the table model
    // Adds buttons for the CRUD operations
    public ProcedureUI() {
        super("Procedure Manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        System.out.println("DEBUG: Initializing ProcedureUI...");

        tableModel = new DefaultTableModel(new String[]{"ID", "Procedure Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
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
        System.out.println("DEBUG: ProcedureUI initialized and displayed.");
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
                // Try to read response stream for confirmation/details, though usually empty for POST/PUT/DELETE
                try {
                    con.getInputStream().close(); 
                    System.out.println("DEBUG: sendRequest (" + method + ") successful for URL: " + urlStr);
                    return "OK";
                } catch (Exception e) {
                    System.out.println("DEBUG: sendRequest (" + method + ") successful but failed to close input stream for URL: " + urlStr);
                    return "OK"; // Still considered success based on status code
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

    // URL-encodes a string param to safely transmit it in an HTTP query
    private String encodeParam(String param) {
        if (param == null)
            return "";
        try {
            String encoded = java.net.URLEncoder.encode(param, "UTF-8");
            System.out.println("DEBUG: encodeParam original: '" + param + "', encoded: '" + encoded + "'");
            return encoded;
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("DEBUG: encodeParam fallback used for: '" + param + "'");
            return param.replace(" ", "%20");
        }
    }


    // Retrieves all procedure records from the backend, and populates the JTable
    private void loadProcedures() {
        System.out.println("DEBUG: loadProcedures started.");
        tableModel.setRowCount(0);

        String json = httpGET(BASE_URL + "/procedures");
        if (json == null) {
            System.out.println("DEBUG: loadProcedures failed to get JSON data.");
            return;
        }
        
        int rowCount = 0;
        try {
            String[] entries = json.replace("[", "").replace("]", "").split("\\},\\{");
            for (String entry : entries) {
                if (entry.isBlank()) continue;
                
                try {
                    long id = Long.parseLong(entry.split("id\":")[1].split(",")[0].replaceAll("[^0-9]", ""));
                    String name = entry.split("procedureName\":\"")[1].split("\"")[0];
    
                    tableModel.addRow(new Object[]{id, name});
                    rowCount++;
                } catch (Exception parseException) {
                    System.out.println("DEBUG: loadProcedures parsing error for entry: " + entry);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "JSON Structure error during parsing: " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: loadProcedures major JSON parsing error.");
        }
        
        System.out.println("DEBUG: loadProcedures finished. Total procedures loaded: " + rowCount);
    }

    // Handles the "Add Procedure" action, promts the user for name, constructs the POST URL,
    // then sends the request, and refreshes the table
    private void addProcedure() {
        String name = JOptionPane.showInputDialog(this, "Enter procedure name:");
        if (name == null || name.isBlank()) {
            System.out.println("DEBUG: addProcedure canceled or rejected.");
            return;
        }

        String url = BASE_URL + "/addProcedure?procedureName=" + encodeParam(name);
        
        httpPOST(url);
        loadProcedures();
        System.out.println("DEBUG: addProcedure completed and table refreshed.");
    }

    // Handles the "Edit Procedure" action, gets selected row data, prompts user for name,
    // constructs the URL, sends PUT request, and then refreshes the table.
    private void editProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            System.out.println("DEBUG: editProcedure failed (no row selected).");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);

        String newName = JOptionPane.showInputDialog(this, "Edit name:", currentName);
        if (newName == null || newName.isBlank()) {
            System.out.println("DEBUG: editProcedure canceled or rejected.");
            return;
        }
        
        String url = BASE_URL + "/upsertProcedure?id=" + id + "&procedureName=" + encodeParam(newName);

        httpPUT(url);
        loadProcedures();
        System.out.println("DEBUG: editProcedure completed and table refreshed for ID: " + id);
    }

    // Handles the "Delete Procedure" action, confirms the deletion of the selected procedure by ID
    // then sneds the delete request, and then refreshes the table
    private void deleteProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            System.out.println("DEBUG: deleteProcedure failed (no row selected).");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete procedure " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("DEBUG: deleteProcedure canceled by user.");
            return;
        }
        
        String url = BASE_URL + "/deleteProcedure?id=" + id;

        httpDELETE(url);
        loadProcedures();
        System.out.println("DEBUG: deleteProcedure completed and table refreshed for ID: " + id);
    }

    // Main entry point
    public static void main(String[] args) {
        System.out.println("DEBUG: Starting ProcedureUI application...");
        SwingUtilities.invokeLater(ProcedureUI::new);
    }
}