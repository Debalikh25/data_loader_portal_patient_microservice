package com.cts.dlt.dao;

import java.util.List;

import com.cts.dlt.entities.InvalidPatient;
import com.cts.dlt.entities.Patient;

public class ResponseAfterUploadDAO {
	
	 private List<Patient> successfullPatients;
	 private List<InvalidPatient> invalidPatients;
	 
	 
	public List<Patient> getSuccessfullPatients() {
		return successfullPatients;
	}
	public void setSuccessfullPatients(List<Patient> successfullPatients) {
		this.successfullPatients = successfullPatients;
	}
	public List<InvalidPatient> getInvalidPatients() {
		return invalidPatients;
	}
	public void setInvalidPatients(List<InvalidPatient> invalidPatients) {
		this.invalidPatients = invalidPatients;
	}
	 

}
