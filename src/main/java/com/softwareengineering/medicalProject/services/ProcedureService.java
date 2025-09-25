package com.softwareengineering.medicalProject.services;

import com.softwareengineering.medicalProject.models.Procedure;
import com.softwareengineering.medicalProject.repositories.ProcedureRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcedureService {
    private final ProcedureRepository procedureRepository;

    public ProcedureService(ProcedureRepository procedureRepository) {
        this.procedureRepository = procedureRepository;
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
        procedure.setId(id);
        procedure.setProcedureName(procedureName);
        return procedureRepository.save(procedure);
    }

    public void deleteProcedure(Long id) {
        procedureRepository.deleteById(id);
    }
}
