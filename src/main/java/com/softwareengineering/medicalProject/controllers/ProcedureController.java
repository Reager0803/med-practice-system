package com.softwareengineering.medicalProject.controllers;

import com.softwareengineering.medicalProject.models.Procedure;
import com.softwareengineering.medicalProject.services.ProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/procedure")
    public Procedure getProcedure(@RequestParam Long id) {
        return procedureService.getProcedureById(id);
    }

    @PostMapping("/addProcedure")
    public Procedure addProcedure(@RequestParam String procedureName) {
        return procedureService.addProcedure(procedureName);
    }

    @PutMapping("/upsertProcedure")
    public Procedure upsertProcedure(@RequestParam(required = false) Long id, @RequestParam String procedureName) {
        return procedureService.upsertProcedure(id, procedureName);
    }

    @DeleteMapping("/deleteProcedure")
    public void deleteProcedure(@RequestParam Long id) {
        procedureService.deleteProcedure(id);
    }
}
