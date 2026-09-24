package com.mercaditocloud.carrito.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.carrito.dto.ActualizarCantidadRequest;
import com.mercaditocloud.carrito.dto.AgregarItemRequest;
import com.mercaditocloud.carrito.dto.CarritoResponse;
import com.mercaditocloud.carrito.exception.SolicitudInvalidaException;
import com.mercaditocloud.carrito.service.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

	static final String USUARIO_HEADER = "X-Usuario-Id";

	private final CarritoService carritoService;

	@GetMapping
	public CarritoResponse obtener(@RequestHeader(value = USUARIO_HEADER, required = false) String usuarioId) {
		return carritoService.obtener(usuario(usuarioId));
	}

	@PostMapping("/items")
	public CarritoResponse agregar(
			@RequestHeader(value = USUARIO_HEADER, required = false) String usuarioId,
			@Valid @RequestBody AgregarItemRequest request) {
		return carritoService.agregar(usuario(usuarioId), request.productoId(), request.cantidad());
	}

	@PutMapping("/items/{productoId}")
	public CarritoResponse actualizarCantidad(
			@RequestHeader(value = USUARIO_HEADER, required = false) String usuarioId,
			@PathVariable Long productoId,
			@Valid @RequestBody ActualizarCantidadRequest request) {
		return carritoService.actualizarCantidad(usuario(usuarioId), productoId, request.cantidad());
	}

	@DeleteMapping("/items/{productoId}")
	public CarritoResponse eliminarItem(
			@RequestHeader(value = USUARIO_HEADER, required = false) String usuarioId,
			@PathVariable Long productoId) {
		return carritoService.eliminarItem(usuario(usuarioId), productoId);
	}

	@DeleteMapping
	public ResponseEntity<CarritoResponse> vaciar(
			@RequestHeader(value = USUARIO_HEADER, required = false) String usuarioId) {
		return ResponseEntity.ok(carritoService.vaciar(usuario(usuarioId)));
	}

	private String usuario(String usuarioId) {
		// Hasta conectar Cognito, el BFF enviará el identificador del usuario.
		if (usuarioId == null || usuarioId.isBlank()) {
			throw new SolicitudInvalidaException("El encabezado X-Usuario-Id es obligatorio");
		}
		return usuarioId.trim();
	}

}
