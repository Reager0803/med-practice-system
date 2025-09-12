package com.softwareengineering.medicalProject.controllers;

import com.softwareengineering.medicalProject.models.PatientHistory;
import com.softwareengineering.medicalProject.services.PatientHistoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class PatientHistoryController {
    private final PatientHistoryService patientHistoryService;

    public PatientHistoryController(PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    @GetMapping("/patientHistoryAll")
    public Iterable<PatientHistory> getPatientHistories() {
        return patientHistoryService.getAllPatientHistories();
    }

    @GetMapping("/patientHistory")
    public PatientHistory getPatientHistory(@RequestParam Long id) {
        return patientHistoryService.getPatientHistory(id);
    }



    @PostMapping("/addPatientHistory")
    public PatientHistory addPatientHistory(@RequestParam Long patientId, @RequestParam Long procedureId, @RequestParam LocalDateTime dateOfProcedure, @RequestParam String doctor) {
        return patientHistoryService.addPatientHistory(patientId, procedureId, dateOfProcedure, doctor);
    }

    @PutMapping("/upsertPatientHistory")
    public PatientHistory upsertPatientHistory(@RequestParam(required = false) Long id, @RequestParam Long patientId, @RequestParam Long procedureId, @RequestParam LocalDateTime dateOfProcedure, @RequestParam String doctor) {
        return patientHistoryService.upsertPatientHistory(id, patientId, procedureId, dateOfProcedure, doctor);
    }

    @DeleteMapping("/deletePatientHistory")
    public void deletePatientHistory(@RequestParam Long id){
        patientHistoryService.deletePatientHistory(id);
    }

}
