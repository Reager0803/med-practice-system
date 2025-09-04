package com.softwareengineering.medicalProject.repositories;

import com.softwareengineering.medicalProject.models.PatientHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface PatientHistoryRepository extends CrudRepository<PatientHistory, Long> {
    List<PatientHistory> findByPatientIDKey(@Param("patientID") Long patientID);
}
