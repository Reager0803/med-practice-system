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

public class ProcedureUI extends JFrame {

    private JTable procedureTable;
    private DefaultTableModel tableModel;

    public ProcedureUI() {
        super("Procedure Manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Procedure Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        procedureTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(procedureTable);

        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Procedure");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        refreshBtn.addActionListener(e -> loadProcedures());
        addBtn.addActionListener(e -> addProcedure());
        editBtn.addActionListener(e -> editProcedure());
        deleteBtn.addActionListener(e -> deleteProcedure());

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

    private String httpGET(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "GET error: " + e.getMessage());
            return null;
        }
    }

    private String httpPOST(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.getInputStream().close();
            return "OK";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "POST error: " + e.getMessage());
            return null;
        }
    }

    private String httpPUT(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.getInputStream().close();
            return "OK";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "PUT error: " + e.getMessage());
            return null;
        }
    }

    private String httpDELETE(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.getInputStream().close();
            return "OK";
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DELETE error: " + e.getMessage());
            return null;
        }
    }

    private void loadProcedures() {
        tableModel.setRowCount(0);

        String json = httpGET("http://localhost:8080/procedures");
        if (json == null) return;
        String[] entries = json.replace("[", "").replace("]", "").split("\\},\\{");
        for (String entry : entries) {
            try {
                long id = Long.parseLong(entry.split("id\":")[1].split(",")[0].replaceAll("[^0-9]", ""));
                String name = entry.split("procedureName\":\"")[1].split("\"")[0];

                tableModel.addRow(new Object[]{id, name});
            } catch (Exception ignore) {}
        }
    }

    private void addProcedure() {
        String name = JOptionPane.showInputDialog(this, "Enter procedure name:");
        if (name == null || name.isBlank()) return;

        httpPOST("http://localhost:8080/addProcedure?procedureName=" + name.replace(" ", "%20"));
        loadProcedures();
    }

    private void editProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);

        String newName = JOptionPane.showInputDialog(this, "Edit name:", currentName);
        if (newName == null || newName.isBlank()) return;

        httpPUT("http://localhost:8080/upsertProcedure?id=" + id + "&procedureName=" + newName.replace(" ", "%20"));
        loadProcedures();
    }

    private void deleteProcedure() {
        int row = procedureTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete procedure " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        httpDELETE("http://localhost:8080/deleteProcedure?id=" + id);
        loadProcedures();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcedureUI::new);
    }
}

