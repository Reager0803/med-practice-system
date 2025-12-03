package com.softwareengineering.medicalProject.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.softwareengineering.medicalProject.models.Patient;
import com.softwareengineering.medicalProject.models.PatientHistory;
import com.softwareengineering.medicalProject.repositories.PatientHistoryRepository;
import com.softwareengineering.medicalProject.repositories.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientHistoryRepository patientHistoryRepository;

    public PatientService(PatientRepository patientRepository, PatientHistoryRepository patientHistoryRepository) {
        this.patientRepository = patientRepository;
        this.patientHistoryRepository = patientHistoryRepository;
    }

    public Iterable<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    public void addPatient(String lastName, String middleName, String firstName, String address, String city,
                           String state, String zip, String phone, Long age, Long height, Long weight, String insurance, String doctor) {
        Patient patient = new Patient(lastName, middleName, firstName, address, city, state, zip, phone, age, height, weight, insurance, doctor);

        patientRepository.save(patient);
    }

    public void upsertPatient(Long id, String lastName, String middleName, String firstName, 
                            String address, String city, String state, String zip, 
                            String phone, Long age, Long height, Long weight, 
                            String insurance, String doctor) {

        Patient patient;

        if (id != null && id != 0L) {
            patient = patientRepository.findById(id).orElse(null);

            if (patient == null) return;
            patient.setLastName(lastName);
            patient.setMiddleName(middleName);
            patient.setFirstName(firstName);
            patient.setAddress(address);
            patient.setCity(city);
            patient.setState(state);
            patient.setZip(zip);
            patient.setPhone(phone);
            patient.setAge(age);
            patient.setHeight(height);
            patient.setWeight(weight);
            patient.setInsurance(insurance);
            patient.setPrimaryCareDoctor(doctor);

        } else {
            patient = new Patient(lastName, middleName, firstName, address, city, 
                                state, zip, phone, age, height, weight, insurance, doctor);
        }

        patientRepository.save(patient);
    }

    public void deletePatient(Long id, String lastName, String middleName, String firstName) {
        Patient patientToDelete = null;

        if (id != 0L) {
            patientToDelete = patientRepository.findById(id).orElse(null);
        } else if (!lastName.isEmpty() && !middleName.isEmpty() && !firstName.isEmpty()) {
            patientToDelete = patientRepository.findByLastNameAndMiddleNameAndFirstName(lastName, middleName, firstName).orElse(null);
        } else if (!lastName.isEmpty() && !firstName.isEmpty()) {
            patientToDelete = patientRepository.findByLastNameAndFirstName(lastName, firstName).orElse(null);
        }

        if (patientToDelete == null) {
            return; 
        }
        
        Long patientId = patientToDelete.getId();
        List<PatientHistory> histories = patientHistoryRepository.findByPatientIDKey(patientId);

        if (!histories.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Cannot delete Patient ID " + patientId + " (" + patientToDelete.getFirstName() + " " + patientToDelete.getLastName() + ") because they have " + histories.size() + " associated history records."
            );
        }
        
        patientRepository.delete(patientToDelete);
    }
}