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

//    public Procedure addProcedure() {
//
//    }
}
