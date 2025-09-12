package com.softwareengineering.medicalProject.repositories;

import com.softwareengineering.medicalProject.models.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {
    Optional<Procedure> findById(Long id);
    Optional<Procedure> findByProcedureName(String procedureName);
}
