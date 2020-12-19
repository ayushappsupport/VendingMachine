package com.vending.bean;

import java.util.Date;

public class ErrorMessage {

	
	private Date errorTime;
	private String message;
	
	public ErrorMessage(Date errorTime,String message) {
		this.errorTime=errorTime;
		this.message=message;
	}

	public Date getErrorTime() {
		return errorTime;
	}

	public void setErrorTime(Date errorTime) {
		this.errorTime = errorTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
