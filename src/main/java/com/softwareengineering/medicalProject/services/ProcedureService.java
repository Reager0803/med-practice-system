package com.softwareengineering.medicalProject.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.softwareengineering.medicalProject.models.PatientHistory;
import com.softwareengineering.medicalProject.models.Procedure;
import com.softwareengineering.medicalProject.repositories.PatientHistoryRepository;
import com.softwareengineering.medicalProject.repositories.ProcedureRepository;

@Service
public class ProcedureService {
    private final ProcedureRepository procedureRepository;
    private final PatientHistoryRepository patientHistoryRepository;

    public ProcedureService(ProcedureRepository procedureRepository, PatientHistoryRepository patientHistoryRepository) {
        this.procedureRepository = procedureRepository;
        this.patientHistoryRepository = patientHistoryRepository;
    }

    public Iterable<Procedure> getAllProcedures() {
        return procedureRepository.findAll();
    }

    public Procedure getProcedureById(Long id) {
        return procedureRepository.findById(id).orElse(null);
    }

    public Procedure getProcedureByName(String procedureName) {
        return procedureRepository.findByProcedureName(procedureName).orElse(null);
    }

    public Procedure addProcedure(String procedureName) {
        Procedure procedure = new Procedure();
        procedure.setProcedureName(procedureName);
        return procedureRepository.save(procedure);
    }

    public Procedure upsertProcedure(Long id, String procedureName) {
        Procedure procedure = procedureRepository.findById(id).orElse(null);
        if (procedure == null) {
            procedure = new Procedure();
        }
        procedure.setProcedureName(procedureName);
        return procedureRepository.save(procedure);
    }

    public void deleteProcedure(Long id) {
        List<PatientHistory> histories = patientHistoryRepository.findByProcedureIDKey(id);
        
        if (!histories.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Cannot delete Procedure ID " + id + " because it has " + histories.size() + " associated history records."
            );
        }

        procedureRepository.deleteById(id);
    }
}