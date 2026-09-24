package com.mercaditocloud.bff.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CarritoClient {

	private static final String SERVICIO = "carrito-service";

	private final RestClient carritoRestClient;
	private final ClienteHttp clienteHttp;

	public CarritoClient(
			@Qualifier("carritoRestClient") RestClient carritoRestClient,
			ClienteHttp clienteHttp) {
		this.carritoRestClient = carritoRestClient;
		this.clienteHttp = clienteHttp;
	}

	public ResponseEntity<String> obtener(String usuarioId) {
		return clienteHttp.ejecutar(
				carritoRestClient, HttpMethod.GET, builder -> builder.path("/api/carrito").build(),
				null, usuarioId, SERVICIO);
	}

	public ResponseEntity<String> agregar(String usuarioId, String cuerpo) {
		return clienteHttp.ejecutar(
				carritoRestClient, HttpMethod.POST, builder -> builder.path("/api/carrito/items").build(),
				cuerpo, usuarioId, SERVICIO);
	}

	public ResponseEntity<String> actualizarCantidad(String usuarioId, Long productoId, String cuerpo) {
		return clienteHttp.ejecutar(
				carritoRestClient,
				HttpMethod.PUT,
				builder -> builder.path("/api/carrito/items/{productoId}").build(productoId),
				cuerpo,
				usuarioId,
				SERVICIO);
	}

	public ResponseEntity<String> eliminarItem(String usuarioId, Long productoId) {
		return clienteHttp.ejecutar(
				carritoRestClient,
				HttpMethod.DELETE,
				builder -> builder.path("/api/carrito/items/{productoId}").build(productoId),
				null,
				usuarioId,
				SERVICIO);
	}

	public ResponseEntity<String> vaciar(String usuarioId) {
		return clienteHttp.ejecutar(
				carritoRestClient, HttpMethod.DELETE, builder -> builder.path("/api/carrito").build(),
				null, usuarioId, SERVICIO);
	}

}
