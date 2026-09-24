package com.mercaditocloud.bff.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String mensaje) {
		super(mensaje);
	}

}
