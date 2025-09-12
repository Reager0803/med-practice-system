package com.softwareengineering.medicalProject.repositories;

import com.softwareengineering.medicalProject.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findById(Long id);

    Optional<Patient> findByLastNameAndMiddleNameAndFirstName(String lastName, String middleName, String firstName);

    Optional<Patient> findByLastNameAndFirstName(String lastName, String firstName);

}
