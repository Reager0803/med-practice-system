package com.softwareengineering.medicalProject.services;

import com.softwareengineering.medicalProject.models.PatientHistory;
import com.softwareengineering.medicalProject.repositories.PatientHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatientHistoryService {
    private final PatientHistoryRepository patientHistoryRepository;

    public PatientHistoryService(PatientHistoryRepository patientHistoryRepository) {
        this.patientHistoryRepository = patientHistoryRepository;
    }

    public Iterable<PatientHistory> getAllPatientHistories() {
        return patientHistoryRepository.findAll();
    }

    public PatientHistory getPatientHistory(Long id) {
//        returns PatientHistory or null (if it can't be found)
        return patientHistoryRepository.findById(id).orElse(null);
    }

    public Iterable<PatientHistory> getAllPatientHistoriesForPatient(Long patientId) {
        return patientHistoryRepository.findByPatientIDKey(patientId);
    }

    public Iterable<PatientHistory> getAllPatientHistoriesForProcedure(Long procedureId) {
//        return patientHistoryRepository.findByProcedureId(procedureId);
        return null;
    }

    public PatientHistory addPatientHistory(Long patientId, Long procedureId, LocalDateTime dateOfProcedure, String doctor) {
        PatientHistory patientHistory = new PatientHistory(patientId, procedureId, dateOfProcedure, doctor);

        patientHistoryRepository.save(patientHistory);
        return patientHistory;
    }

    public PatientHistory upsertPatientHistory(Long id, Long patientId, Long procedureId, LocalDateTime dateOfProcedure, String doctor) {
        PatientHistory patientHistory = patientHistoryRepository.findById(id).orElse(null);

        if (patientHistory != null) {
            patientHistory.setPatientIDKey(patientId);
            patientHistory.setProcedureIDKey(procedureId);
            patientHistory.setDateOfProcedure(dateOfProcedure);
            patientHistory.setDoctor(doctor);
        } else {
            patientHistory = new PatientHistory(patientId, procedureId, dateOfProcedure, doctor);
        }

        patientHistoryRepository.save(patientHistory);
        return patientHistory;
    }

    public void deletePatientHistory(Long id) {
        patientHistoryRepository.deleteById(id);
    }
}
