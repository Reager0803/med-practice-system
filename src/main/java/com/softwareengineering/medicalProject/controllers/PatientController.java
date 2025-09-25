package com.softwareengineering.medicalProject.controllers;

import com.softwareengineering.medicalProject.models.Patient;
import com.softwareengineering.medicalProject.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientController {
    @Autowired
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public Iterable<Patient> getPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/patient")
    public Patient getPatient(@RequestParam Long id) {
        return patientService.getPatientById(id);
    }

    @PostMapping("/addPatient")
    public void addPatient(@RequestParam String lastName, @RequestParam String middleName, @RequestParam String firstName, @RequestParam String address,
    @RequestParam String city, @RequestParam String state, @RequestParam String zip, @RequestParam String phone, @RequestParam Long age, @RequestParam Long height, @RequestParam Long weight,
    @RequestParam String insurance, @RequestParam String doctor) {
        patientService.addPatient(lastName, middleName, firstName, address, city, state, zip, phone, age, height, weight, insurance, doctor);
    }

    @PutMapping("/addPatient")
    public void upsertPatient(@RequestParam Long id, @RequestParam String lastName, @RequestParam String middleName, @RequestParam String firstName, @RequestParam String address,
                           @RequestParam String city, @RequestParam String state, @RequestParam String zip, @RequestParam String phone, @RequestParam Long age, @RequestParam Long height, @RequestParam Long weight,
                           @RequestParam String insurance, @RequestParam String doctor) {
        patientService.upsertPatient(id, lastName, middleName, firstName, address, city, state, zip, phone, age, height, weight, insurance, doctor);
    }

    @DeleteMapping("/removePatient")
    public void deletePatient(@RequestParam(required = false) Long id, @RequestParam String lastName, @RequestParam(required = false) String middleName, @RequestParam String firstName) {
        patientService.deletePatient(id, lastName, middleName, firstName);
    }
}
