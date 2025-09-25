package com.softwareengineering.medicalProject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
//@Table(name = "patientHistory")
public class PatientHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patientID")
    private Long patientIDKey;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Long procedureIDKey;

    private LocalDateTime dateOfProcedure;

    private String doctor;

    public PatientHistory() {
    }

    public PatientHistory(Long id, Long patientIDKey, Long procedureIDKey, LocalDateTime dateOfProcedure, String doctor) {
        this.id = id;
        this.patientIDKey = patientIDKey;
        this.procedureIDKey = procedureIDKey;
        this.dateOfProcedure = dateOfProcedure;
        this.doctor = doctor;
    }

    public PatientHistory(Long patientIDKey, Long procedureIDKey, LocalDateTime dateOfProcedure, String doctor) {
        this.patientIDKey = patientIDKey;
        this.procedureIDKey = procedureIDKey;
        this.dateOfProcedure = dateOfProcedure;
        this.doctor = doctor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientIDKey() {
        return patientIDKey;
    }

    public void setPatientIDKey(Long patientIDKey) {
        this.patientIDKey = patientIDKey;
    }

    public Long getProcedureIDKey() {
        return procedureIDKey;
    }

    public void setProcedureIDKey(Long procedureIDKey) {
        this.procedureIDKey = procedureIDKey;
    }

    public LocalDateTime getDateOfProcedure() {
        return dateOfProcedure;
    }

    public void setDateOfProcedure(LocalDateTime dateOfProcedure) {
        this.dateOfProcedure = dateOfProcedure;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}
