package com.mercaditocloud.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.bff.client.CarritoClient;
import com.mercaditocloud.bff.exception.SolicitudInvalidaException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

	private final CarritoClient carritoClient;

	@GetMapping
	public ResponseEntity<String> obtener(Authentication autenticacion) {
		return carritoClient.obtener(usuario(autenticacion));
	}

	@PostMapping("/items")
	public ResponseEntity<String> agregar(Authentication autenticacion, @RequestBody String cuerpo) {
		return carritoClient.agregar(usuario(autenticacion), cuerpo);
	}

	@PutMapping("/items/{productoId}")
	public ResponseEntity<String> actualizarCantidad(
			Authentication autenticacion,
			@PathVariable Long productoId,
			@RequestBody String cuerpo) {
		return carritoClient.actualizarCantidad(usuario(autenticacion), productoId, cuerpo);
	}

	@DeleteMapping("/items/{productoId}")
	public ResponseEntity<String> eliminarItem(Authentication autenticacion, @PathVariable Long productoId) {
		return carritoClient.eliminarItem(usuario(autenticacion), productoId);
	}

	@DeleteMapping
	public ResponseEntity<String> vaciar(Authentication autenticacion) {
		return carritoClient.vaciar(usuario(autenticacion));
	}

	private String usuario(Authentication autenticacion) {
		if (autenticacion instanceof JwtAuthenticationToken token && token.getToken().getSubject() != null) {
			return token.getToken().getSubject();
		}
		throw new SolicitudInvalidaException("No se pudo identificar al usuario");
	}

}
