package com.cts.dlt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.dlt.entities.InvalidPatient;

public interface InvalidPatientRepositry extends JpaRepository<InvalidPatient, Long> {

}
