package com.softwareengineering.medicalProject.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.softwareengineering.medicalProject.models.PatientHistory;

@RepositoryRestResource
public interface PatientHistoryRepository extends JpaRepository<PatientHistory, Long> {
    List<PatientHistory> findByPatientIDKey(@Param("patientID") Long patientID);

    List<PatientHistory> findByProcedureIDKey(@Param("procedureId") Long procedureID);
}
