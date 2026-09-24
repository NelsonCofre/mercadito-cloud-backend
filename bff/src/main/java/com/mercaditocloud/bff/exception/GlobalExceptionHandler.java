package com.mercaditocloud.bff.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> noEncontrado(ResourceNotFoundException ex) {
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(SolicitudInvalidaException.class)
	public ResponseEntity<ApiError> solicitud(SolicitudInvalidaException ex) {
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ServicioNoDisponibleException.class)
	public ResponseEntity<ApiError> noDisponible(ServicioNoDisponibleException ex) {
		return respuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> general(Exception ex) {
		log.error("Error no controlado", ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");
	}

	private ResponseEntity<ApiError> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(new ApiError(status.value(), mensaje, List.of()));
	}

}
