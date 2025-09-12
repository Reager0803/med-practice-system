package com.softwareengineering.medicalProject.controllers;

import com.softwareengineering.medicalProject.models.Procedure;
import com.softwareengineering.medicalProject.services.ProcedureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcedureController {
    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping
    public Iterable<Procedure> getProcedures() {
        return procedureService.getAllProcedures();
    }
}
