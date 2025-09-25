package com.softwareengineering.medicalProject.controllers;

import com.softwareengineering.medicalProject.models.Procedure;
import com.softwareengineering.medicalProject.services.ProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcedureController {

    @Autowired
    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping("/procedures")
    public Iterable<Procedure> getProcedures() {
        return procedureService.getAllProcedures();
    }
}
