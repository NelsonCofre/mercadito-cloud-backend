package com.mercaditocloud.bff.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductosClient {

	private static final String SERVICIO = "productos-service";

	private final RestClient productosRestClient;
	private final ClienteHttp clienteHttp;

	public ProductosClient(
			@Qualifier("productosRestClient") RestClient productosRestClient,
			ClienteHttp clienteHttp) {
		this.productosRestClient = productosRestClient;
		this.clienteHttp = clienteHttp;
	}

	public ResponseEntity<String> listar(String q, Long categoriaId, Boolean activo) {
		return clienteHttp.ejecutar(productosRestClient, HttpMethod.GET, builder -> {
			var uri = builder.path("/api/productos");
			if (q != null && !q.isBlank()) {
				uri.queryParam("q", q);
			}
			if (categoriaId != null) {
				uri.queryParam("categoriaId", categoriaId);
			}
			if (activo != null) {
				uri.queryParam("activo", activo);
			}
			return uri.build();
		}, null, null, SERVICIO);
	}

	public ResponseEntity<String> obtener(Long id) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.GET, builder -> builder.path("/api/productos/{id}").build(id),
				null, null, SERVICIO);
	}

	public ResponseEntity<String> crear(String cuerpo) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.POST, builder -> builder.path("/api/productos").build(),
				cuerpo, null, SERVICIO);
	}

	public ResponseEntity<String> actualizar(Long id, String cuerpo) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.PUT, builder -> builder.path("/api/productos/{id}").build(id),
				cuerpo, null, SERVICIO);
	}

	public ResponseEntity<String> desactivar(Long id) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.DELETE, builder -> builder.path("/api/productos/{id}").build(id),
				null, null, SERVICIO);
	}

	public ResponseEntity<String> listarCategorias() {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.GET, builder -> builder.path("/api/categorias").build(),
				null, null, SERVICIO);
	}

	public ResponseEntity<String> obtenerCategoria(Long id) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.GET, builder -> builder.path("/api/categorias/{id}").build(id),
				null, null, SERVICIO);
	}

	public ResponseEntity<String> crearCategoria(String cuerpo) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.POST, builder -> builder.path("/api/categorias").build(),
				cuerpo, null, SERVICIO);
	}

	public ResponseEntity<String> actualizarCategoria(Long id, String cuerpo) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.PUT, builder -> builder.path("/api/categorias/{id}").build(id),
				cuerpo, null, SERVICIO);
	}

	public ResponseEntity<String> eliminarCategoria(Long id) {
		return clienteHttp.ejecutar(
				productosRestClient, HttpMethod.DELETE, builder -> builder.path("/api/categorias/{id}").build(id),
				null, null, SERVICIO);
	}

}
