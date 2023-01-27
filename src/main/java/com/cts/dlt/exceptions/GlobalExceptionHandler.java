package com.cts.dlt.exceptions;

import java.io.UncheckedIOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import com.cts.dlt.dao.ErrorDAO;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private  ErrorDAO error = new ErrorDAO();
	
	@ExceptionHandler(UncheckedIOException.class)
	public ResponseEntity<?> uncheckedIoException(UncheckedIOException e){
		 
		error.setError(e.getMessage());
		
		return new  ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
		 
		
		 
	}
	
	   @ExceptionHandler(value = MultipartException.class)
	   public ResponseEntity<?> handleFileUploadingError(MultipartException exception) {
	        error.setError(exception.getMessage());
	        return new  ResponseEntity<>(error , HttpStatus.INTERNAL_SERVER_ERROR);
	    }

}
