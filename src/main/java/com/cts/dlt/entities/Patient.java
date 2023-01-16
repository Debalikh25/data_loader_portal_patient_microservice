package com.cts.dlt.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Patient {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long patientId;
	
private String patientName;
	
	private String patientAddress;
	
	private String patientDateOfBirth;
	
	private String patientEmail;
	
	private Long patientContactNumber;
	
	private String patientDrugId;
	
	private String patientDrugName;
	
	private String status;
	
	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientAddress() {
		return patientAddress;
	}

	public void setPatientAddress(String patientAddress) {
		this.patientAddress = patientAddress;
	}

	public String getPatientDateOfBirth() {
		return patientDateOfBirth;
	}

	public void setPatientDateOfBirth(String patientDateOfBirth) {
		this.patientDateOfBirth = patientDateOfBirth;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public Long getPatientContactNumber() {
		return patientContactNumber;
	}

	public void setPatientContactNumber(Long patientContactNumber) {
		this.patientContactNumber = patientContactNumber;
	}

	public String getPatientDrugId() {
		return patientDrugId;
	}

	public void setPatientDrugId(String patientDrugId) {
		this.patientDrugId = patientDrugId;
	}

	public String getPatientDrugName() {
		return patientDrugName;
	}

	public void setPatientDrugName(String patientDrugName) {
		this.patientDrugName = patientDrugName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

}
