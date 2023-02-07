package com.cts.dlt.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;

//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cts.dlt.dao.ResponseAfterUploadDAO;
import com.cts.dlt.entities.InvalidPatient;
import com.cts.dlt.entities.Patient;
import com.cts.dlt.helper.StringConstants;
import com.cts.dlt.repositories.InvalidPatientRepositry;
import com.cts.dlt.repositories.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
	private PatientRepository repo;

	@Autowired
	private InvalidPatientRepositry ipr;

	// Get Single Patient By Id
	public Patient getPatientById(long id) {
		Patient patient = this.repo.findByPatientId(id);
		if (patient != null) {
			return patient;
		}
		return null;
	}

	// Get Single Patient By Name
	public List<Patient> getPatientByName(String name) {
		return this.repo.findByPatientName(name);

	}

	// Function to Upload Excel File and get Patient Details and save in DB.
	public ResponseAfterUploadDAO uploadFile(String path, MultipartFile file) throws IOException {

		ResponseAfterUploadDAO response = new ResponseAfterUploadDAO();

		Date date = new Date();

		String name = file.getOriginalFilename().split("\\.")[0];
		name += "" + date.getTime() + "." + file.getOriginalFilename().split("\\.")[1];

		String fullPath = path + File.separator + name;

		ArrayList<Patient> successFullPatients = new ArrayList<Patient>();
		ArrayList<InvalidPatient> failedPatients = new ArrayList<InvalidPatient>();

		File f = new File(path);

		if (!f.exists()) {
			f.mkdir();
		}

		XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());

		XSSFSheet sheet = wb.getSheetAt(0);
	//	int rowCount = sheet.getLastRowNum();
		DataFormatter formatter = new DataFormatter();

		for (Row row : sheet) {

			Patient patient = new Patient();
			InvalidPatient invalidPatient = new InvalidPatient();
			String errorSentence = "";

			String patientName = formatter.formatCellValue(row.getCell(0));
			String address = formatter.formatCellValue(row.getCell(1));
			String dob = formatter.formatCellValue(row.getCell(2));
			String email = formatter.formatCellValue(row.getCell(3));
			String contactNumber = formatter.formatCellValue(row.getCell(4));
			String drugId = formatter.formatCellValue(row.getCell(5));
			String drugName = formatter.formatCellValue(row.getCell(6));

			Patient p = this.repo.findByPatientEmail(email);
			if (p != null) {
				invalidPatient.setPatientEmail(email);
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence += " "+StringConstants.PATIENT_EMAIL_EXIST+" - "; 
			}

			// start of validations
			if (!(patientName.matches("^[a-zA-Z ]*$")) || name.length() > 30 || name.length() < 5) {
				invalidPatient.setPatientName(patientName);
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence +=" "+StringConstants.INVALID_PATIENT_NAME+" - ";
			}

			if (!(contactNumber.length() == 10)) {
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence +=" "+StringConstants.INVALID_PATIENT_CONTACT+" - ";
			}

			if (!(email.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))) {
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence += " "+StringConstants.INVALID_PATIENT_EMAIL+" - ";;
			}
			
			if(address.trim().isEmpty()){
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence +=" "+StringConstants.INVALID_PATIENT_ADDRESS+" - ";;
			}

			if (!(drugId.matches("\\d{5}-\\d{4}-\\d{2}"))) {
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence += " "+StringConstants.INVALID_PATIENT_DRUG_ID+" - ";;
			}

			if (!(dob.matches("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$"))) {
				invalidPatient.setStatus(StringConstants.FAILED);
				errorSentence += " "+StringConstants.INVALID_PATIENT_DOB+" - ";
			} else {

				boolean res = calculateDifferenceBetweenDates(dob);

				if (res == false) {
					invalidPatient.setPatientDateOfBirth(dob);
					invalidPatient.setStatus(StringConstants.FAILED);
					errorSentence += " "+StringConstants.INVALID_PATIENT_FUTURE_DOB+" - ";
				}
			}
			// end of validations

			if (invalidPatient.getStatus() != null && invalidPatient.getStatus().equals(StringConstants.FAILED)) {

				invalidPatient.setPatientName(patientName);
				invalidPatient.setPatientAddress(address);
				invalidPatient.setPatientDateOfBirth(dob);
				invalidPatient.setPatientEmail(email);
				invalidPatient.setErrorMessage(errorSentence);
				invalidPatient.setPatientContactNumber(Long.parseLong(contactNumber));
				invalidPatient.setPatientDrugId(drugId);
				invalidPatient.setPatientDrugName(drugName);
				invalidPatient.setStatus(invalidPatient.getStatus());
				InvalidPatient savedInvalidPatient = this.ipr.save(invalidPatient);
				failedPatients.add(savedInvalidPatient);

			} else {

				patient.setPatientName(patientName);
				patient.setPatientAddress(address);
				patient.setPatientDateOfBirth(dob);
				patient.setPatientEmail(email);
				patient.setPatientContactNumber(Long.parseLong(contactNumber));
				patient.setPatientDrugId(drugId);
				patient.setPatientDrugName(drugName);
				patient.setStatus(StringConstants.INDUCTED);
				Patient savedValidPatient = this.repo.save(patient);
				successFullPatients.add(savedValidPatient);

			}

		}

		response.setSuccessfullPatients(successFullPatients);
		response.setInvalidPatients(failedPatients);

		// saving the file in our server
		Files.copy(file.getInputStream(), Paths.get(fullPath));

		return response;
	}

	// Function To Update Patient.
	public String updatePatient(Patient pat) {

		Optional<Patient> exist = this.repo.findById(pat.getPatientId());

		if (exist.isPresent()) {

			if (exist.get().getStatus().equals(StringConstants.PROCESSED)) {
				return StringConstants.NOT_FOUND;
			}

			if (!(pat.getPatientDateOfBirth().matches("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$"))) {
				return StringConstants.INVALID_PATIENT_DOB;
			}

			if (calculateDifferenceBetweenDates(pat.getPatientDateOfBirth()) == false) {
				return StringConstants.INVALID_PATIENT_FUTURE_DOB;
			}

			Long contact = pat.getPatientContactNumber();

			if (contact.toString().length() > 10 || contact.toString().length() < 10) {

				return StringConstants.INVALID_PATIENT_CONTACT;
			}
			
			if(pat.getPatientAddress().trim().isEmpty()){
				return StringConstants.INVALID_PATIENT_ADDRESS;
			}

			if (!(pat.getPatientEmail().matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"))) {
				return StringConstants.INVALID_PATIENT_EMAIL;
			}

			Patient patient = exist.get();
			patient.setPatientEmail(pat.getPatientEmail());
			patient.setPatientContactNumber(pat.getPatientContactNumber());
			patient.setPatientAddress(pat.getPatientAddress());
			patient.setPatientDateOfBirth(pat.getPatientDateOfBirth());
			this.repo.save(patient);

			return "";
		}

		return null;
	}
	
	

	// Patient Sent to Downstream System for Processing
	public String processPatient(Patient patient) {

		Optional<Patient> found = this.repo.findById(patient.getPatientId());
		if(found.isEmpty()) {
			return null;
		}
		
         Patient pat = found.get();
		
		pat.setStatus(StringConstants.PROCESSED);
		this.repo.save(pat);

		return StringConstants.PATIENT_PROCESSED;

	}
	
	

	// helper function to calculate number of days between two dates
	private boolean calculateDifferenceBetweenDates(String dob) {

		try {

			Date today = new Date();
			Date dateOfBirth = new SimpleDateFormat(StringConstants.DATE_FORMAT).parse(dob);
			long difference_In_Time = today.getTime() - dateOfBirth.getTime();

			long difference = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

			if (difference < 1) {
				return false;
			}

			return true;

		} catch (Exception e) {
			return false;
		}

	}

}
