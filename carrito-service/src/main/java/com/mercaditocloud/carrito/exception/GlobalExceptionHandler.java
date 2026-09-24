package com.mercaditocloud.carrito.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> noEncontrado(ResourceNotFoundException ex) {
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
	}

	@ExceptionHandler(ConflictoNegocioException.class)
	public ResponseEntity<ApiError> conflicto(ConflictoNegocioException ex) {
		return respuesta(HttpStatus.CONFLICT, ex.getMessage(), List.of());
	}

	@ExceptionHandler(SolicitudInvalidaException.class)
	public ResponseEntity<ApiError> solicitud(SolicitudInvalidaException ex) {
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
	}

	@ExceptionHandler(ProductoServiceNoDisponibleException.class)
	public ResponseEntity<ApiError> productosNoDisponible(ProductoServiceNoDisponibleException ex) {
		return respuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex) {
		List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.toList();
		return respuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles);
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ApiError> solicitudIlegible(Exception ex) {
		return respuesta(HttpStatus.BAD_REQUEST, "La solicitud no tiene un formato válido", List.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> general(Exception ex) {
		log.error("Error no controlado", ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", List.of());
	}

	private ResponseEntity<ApiError> respuesta(HttpStatus status, String mensaje, List<String> detalles) {
		return ResponseEntity.status(status).body(new ApiError(status.value(), mensaje, detalles));
	}

}
