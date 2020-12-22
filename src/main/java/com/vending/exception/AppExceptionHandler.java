package com.vending.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.vending.bean.ErrorMessage;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleAnyException(Exception ex,WebRequest request){
		String errormsgDescription=ex.getLocalizedMessage();
		if(errormsgDescription==null) errormsgDescription=ex.toString();
		
		ErrorMessage msg=new ErrorMessage(new Date(), errormsgDescription);
		return new ResponseEntity<>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {UserServiceException.class})
	public ResponseEntity<Object> handleUserServiceException(UserServiceException ex,WebRequest request){
		String errormsgDescription=ex.getLocalizedMessage();
		if(errormsgDescription==null) errormsgDescription=ex.toString();
		
		ErrorMessage msg=new ErrorMessage(new Date(), errormsgDescription);
		return new ResponseEntity<>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {BadRequestException.class})
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex,WebRequest request){
		String errormsgDescription=ex.getLocalizedMessage();
		if(errormsgDescription==null) errormsgDescription=ex.toString();
		
		ErrorMessage msg=new ErrorMessage(new Date(), errormsgDescription);
		return new ResponseEntity<>(msg,HttpStatus.BAD_REQUEST);
	}
}
