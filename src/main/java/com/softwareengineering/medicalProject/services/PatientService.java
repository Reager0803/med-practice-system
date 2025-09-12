package com.softwareengineering.medicalProject.services;

import com.softwareengineering.medicalProject.models.Patient;
import com.softwareengineering.medicalProject.repositories.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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

    public void upsertPatient(Long id, String lastName, String middleName, String firstName, String address, String city,
                           String state, String zip, String phone, Long age, Long height, Long weight, String insurance, String doctor) {
        Patient patient = null;
        if (id != 0L) {
            patient = patientRepository.findById(id).orElse(null);
        } else {
            patient = new Patient(lastName, middleName, firstName, address, city, state, zip, phone, age, height, weight, insurance, doctor);
        }

        if (patient != null) {
            patientRepository.save(patient);
        }
    }

    public void deletePatient(Long id, String lastName, String middleName, String firstName) {
        Patient patient = null;
        if (id != 0L) {
            patientRepository.deleteById(id);
        } else if (!lastName.isEmpty() && !middleName.isEmpty() && !firstName.isEmpty()) {
            patient = patientRepository.findByLastNameAndMiddleNameAndFirstName(lastName, middleName, firstName).orElse(null);
        } else if (!lastName.isEmpty() && !firstName.isEmpty()) {
            patient = patientRepository.findByLastNameAndFirstName(lastName, firstName).orElse(null);
        }

        if (patient != null) {
            patientRepository.delete(patient);
        }

    }
}
