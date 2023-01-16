package com.cts.dlt.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cts.dlt.dao.ResponseAfterUploadDAO;
import com.cts.dlt.entities.Patient;

public interface PatientService {
	
	 Patient getPatientById(long id);
	 
	List<Patient> getPatientByName(String name);
	
	ResponseAfterUploadDAO uploadFile(String path, MultipartFile file) throws IOException ;
	
	String updatePatient(Patient p);
	
	String processPatient(Patient p);

}
