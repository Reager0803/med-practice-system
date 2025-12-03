package com.softwareengineering.medicalProject.services;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.softwareengineering.medicalProject.models.PatientHistory;
import com.softwareengineering.medicalProject.repositories.PatientHistoryRepository;
import com.softwareengineering.medicalProject.repositories.PatientRepository;
import com.softwareengineering.medicalProject.repositories.ProcedureRepository;

@Service
public class PatientHistoryService {
    private final PatientHistoryRepository patientHistoryRepository;
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;

    public PatientHistoryService(PatientHistoryRepository patientHistoryRepository, PatientRepository patientRepository, ProcedureRepository procedureRepository) {
        this.patientHistoryRepository = patientHistoryRepository;
        this.patientRepository = patientRepository;
        this.procedureRepository = procedureRepository;
    }

    public Iterable<PatientHistory> getAllPatientHistories() {
        return patientHistoryRepository.findAll();
    }

    public PatientHistory getPatientHistory(Long id) {
        return patientHistoryRepository.findById(id).orElse(null);
    }

    public Iterable<PatientHistory> getAllPatientHistoriesForPatient(Long patientId) {
        return patientHistoryRepository.findByPatientIDKey(patientId);
    }

    public Iterable<PatientHistory> getAllPatientHistoriesForProcedure(Long procedureId) {
        return null;
    }

    public PatientHistory addPatientHistory(Long patientId, Long procedureId, LocalDateTime dateOfProcedure, String doctor) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient ID " + patientId + " does not exist.");
        }
        if (!procedureRepository.existsById(procedureId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Procedure ID " + procedureId + " does not exist.");
        }

        PatientHistory patientHistory = new PatientHistory(patientId, procedureId, dateOfProcedure, doctor);

        patientHistoryRepository.save(patientHistory);
        return patientHistory;
    }

    public PatientHistory upsertPatientHistory(Long id, Long patientId, Long procedureId, LocalDateTime dateOfProcedure, String doctor) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient ID " + patientId + " does not exist.");
        }
        if (!procedureRepository.existsById(procedureId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Procedure ID " + procedureId + " does not exist.");
        }
        
        PatientHistory patientHistory = null;
        if (id != null) {
            patientHistory = patientHistoryRepository.findById(id).orElse(null);
        }

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