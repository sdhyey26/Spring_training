package com.SpringSecurity.Exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserApiException extends RuntimeException{

	private HttpStatus status;
	private String message;
}
