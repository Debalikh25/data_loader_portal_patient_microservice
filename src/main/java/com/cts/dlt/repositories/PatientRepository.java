package com.cts.dlt.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cts.dlt.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	
	 @Query(value="SELECT * from patient where patient_name =:name and status = 'Inducted'" , nativeQuery= true)
	 List<Patient> findByPatientName(@Param("name") String name);
	 
	  @Query(value="SELECT * from patient where patient_id =:id and status = 'Inducted'" , nativeQuery=true)
	  Patient findByPatientId(@Param("id") long id);
	
	 Patient findByPatientEmail(String email);
	 
	 
	 

}
