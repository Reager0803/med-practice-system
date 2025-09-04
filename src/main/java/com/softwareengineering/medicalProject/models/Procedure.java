package com.softwareengineering.medicalProject.models;

import jakarta.persistence.*;

@Entity
//@Table(name = "procedure")
public class Procedure {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String procedureName;

    public Procedure() {}

    public Procedure(Long id, String procedureName) {
        this.id = id;
        this.procedureName = procedureName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }
}
