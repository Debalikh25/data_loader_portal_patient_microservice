package com.cts.dlt.controllers;

import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cts.dlt.clients.FeignUtil;
import com.cts.dlt.dao.ErrorDAO;
import com.cts.dlt.dao.JwtExpired;
import com.cts.dlt.dao.MessageDAO;
import com.cts.dlt.dao.ResponseAfterUploadDAO;
import com.cts.dlt.entities.Patient;
import com.cts.dlt.helper.StringConstants;
import com.cts.dlt.services.PatientServiceImpl;

@RestController
@CrossOrigin()
@RequestMapping("/api/v1/")
public class PatientController {

	@Value("${patient.files}")
	private String filePath;

	@Autowired
	private PatientServiceImpl service;

	@Autowired
	private FeignUtil util;

	private MessageDAO message = new MessageDAO();

	private ErrorDAO error = new ErrorDAO();

	@GetMapping("/expired")
	public ResponseEntity<?> tokenExpired(@RequestHeader(name = "auth", required = false) String header) {
		
		
			
			if (header == null) {
				error.setError(StringConstants.UNAUTHORIZED);
				return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
			}
			JwtExpired valid = this.util.validToken(header);
			if (valid == null) {
				error.setError(StringConstants.UNAUTHORIZED);
				return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
			}

			return new ResponseEntity<>(valid, HttpStatus.OK);
	
	}

	@GetMapping("/patient/{id}")
	public ResponseEntity<?> getPatientById(@PathVariable("id") long id,
			@RequestHeader(name = "auth", required = false) String header) {

		if (header == null) {
			error.setError(StringConstants.UNAUTHORIZED);
			return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
		}
		
		JwtExpired valid = this.util.validToken(header);
		if(valid.isExpired() == true){
			return new ResponseEntity<>(valid, HttpStatus.UNAUTHORIZED);
		}

		Patient patient = this.service.getPatientById(id);
		if (patient == null) {
			message.setMessage(StringConstants.NOT_FOUND);
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(patient, HttpStatus.OK);
	}

	@GetMapping("/patient/name/{name}")
	public ResponseEntity<?> getPatientByName(@PathVariable("name") String name,
			@RequestHeader(name = "auth", required = false) String header) {

		if (header == null) {
			error.setError(StringConstants.UNAUTHORIZED);
			return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
		}
		
		JwtExpired valid = this.util.validToken(header);
		if(valid.isExpired() == true){
			return new ResponseEntity<>(valid, HttpStatus.UNAUTHORIZED);
		}
		
		List<Patient> patients = this.service.getPatientByName(name);
		if (patients.size() == 0) {
			message.setMessage(StringConstants.NOT_FOUND);
			return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(patients, HttpStatus.OK);
	}
	
	

	@PutMapping("/patient/updatepatient")
	public ResponseEntity<?> updatePatient(@RequestBody() Patient patient,
			@RequestHeader(name = "auth", required = false) String header) {
		if (header == null) {
			error.setError(StringConstants.UNAUTHORIZED);
			return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
		}
		
		
		
		JwtExpired valid = this.util.validToken(header);
		if(valid.isExpired() == true){
			return new ResponseEntity<>(valid, HttpStatus.UNAUTHORIZED);
		}

		String updatePatient = this.service.updatePatient(patient);
		if (updatePatient.equals("")) {
			message.setMessage(StringConstants.UPDATED);
			return new ResponseEntity<>(message, HttpStatus.OK);

		}

		if (updatePatient.equals(null)) {
			error.setError(StringConstants.NOT_FOUND);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		error.setError(updatePatient);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
	
	

	@PutMapping("/patient/process")
	public ResponseEntity<?> processPatient(@RequestBody() Patient patient,
			@RequestHeader(name = "auth", required = false) String header) {

		if (header == null) {
			error.setError(StringConstants.UNAUTHORIZED);
			return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
		}
		
		JwtExpired valid = this.util.validToken(header);
		if(valid.isExpired() == true){
			return new ResponseEntity<>(valid, HttpStatus.UNAUTHORIZED);
		}
		
		String res = this.service.processPatient(patient);

		if (res == null) {
			error.setError(StringConstants.NOT_FOUND);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		message.setMessage(res);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file , @RequestHeader(name="Authorization" , required=false) String header) throws UncheckedIOException {
          
		 //Multipart form-data upload accepts in-built headers and not custom headers
		 // like "Authorization" and not 'auth'

		try {
			if (header == null) {
				error.setError(StringConstants.UNAUTHORIZED);
				return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
			}
			
			JwtExpired valid = this.util.validToken(header);
			if(valid.isExpired() == true){
				return new ResponseEntity<>(valid, HttpStatus.UNAUTHORIZED);
			}
			
			

			String ext = file.getOriginalFilename().split("\\.")[1];
			if (!(ext.equals("xlsx") || !(ext.equals("xls")))) {
				message.setMessage(StringConstants.FILE_ERROR);
				return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
			}

			ResponseAfterUploadDAO res = this.service.uploadFile(filePath, file);

			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			message.setMessage(StringConstants.SERVER_ERROR);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
