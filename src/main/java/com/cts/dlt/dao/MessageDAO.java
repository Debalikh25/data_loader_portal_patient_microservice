package com.cts.dlt.dao;

public class MessageDAO {
	
	private String message;
	
	public MessageDAO(){
		
	}
	
	public MessageDAO(String m){
		this.message  = m;
	}
	
	public void setMessage(String m){
		this.message = m;
	}
	
	public String getMessage(){
		return this.message;
	}

}
