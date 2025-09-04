package com.softwareengineering.medicalProject.repositories;

import com.softwareengineering.medicalProject.models.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface PatientRepository extends CrudRepository<Patient, Long> {
    Optional<Patient> findById(Long id);
}
