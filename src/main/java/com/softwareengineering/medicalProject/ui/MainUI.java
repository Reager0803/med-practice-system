package com.softwareengineering.medicalProject.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainUI() {
        setTitle("Dental Clinic Management");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel();
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(800, 100));
        JLabel title = new JLabel("Dental Clinic Management System", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new GridBagLayout());
        add(menuPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);

        JButton patientsButton = createCardButton("Patients", new Color(76, 175, 80));
        JButton historyButton = createCardButton("Patient History", new Color(255, 193, 7));
        JButton proceduresButton = createCardButton("Procedures", new Color(244, 67, 54));
        
        gbc.gridx = 0; gbc.gridy = 0;
        menuPanel.add(patientsButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        menuPanel.add(historyButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        menuPanel.add(proceduresButton, gbc);

        patientsButton.addActionListener(e -> openPatientUI());
        proceduresButton.addActionListener(e -> openProcedureUI());
        historyButton.addActionListener(e -> openPatientHistoryUI());
        setVisible(true);
    }

    private JButton createCardButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 150));
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(color); }
        });

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0,30), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return button;
    }

    private void openPatientUI() {
        JDialog patientDialog = new JDialog(this, "Patient Management", true);
        PatientUI patientUI = new PatientUI();

        patientDialog.setContentPane(patientUI.getContentPane());
        patientDialog.setSize(patientUI.getSize());
        patientDialog.setLocationRelativeTo(this);

        patientUI.dispose();
        patientDialog.setVisible(true);
    }

    private void openProcedureUI() {
        JDialog procedureDialog = new JDialog(this, "Procedure Management", true);
        ProcedureUI procedureUI = new ProcedureUI();

        procedureDialog.setContentPane(procedureUI.getContentPane());
        procedureDialog.setSize(procedureUI.getSize());
        procedureDialog.setLocationRelativeTo(this);

        procedureUI.dispose();
        procedureDialog.setVisible(true);
    }

    private void openPatientHistoryUI() {
        JDialog patientHistoryDialog = new JDialog(this, "Patient History Management", true);
        PatientHistoryUI patientHistoryUI = new PatientHistoryUI();

        patientHistoryDialog.setContentPane(patientHistoryUI.getContentPane());
        patientHistoryDialog.setSize(patientHistoryUI.getSize());
        patientHistoryDialog.setLocationRelativeTo(this);

        patientHistoryUI.dispose();
        patientHistoryDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}

