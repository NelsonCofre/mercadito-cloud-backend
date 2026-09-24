package com.mercaditocloud.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.bff.client.ProductosClient;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final ProductosClient productosClient;
	private final ObjectMapper objectMapper;

	@GetMapping("/productos")
	public ResponseEntity<String> productos(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) Boolean activo) {
		return productosClient.listar(q, categoriaId, activo);
	}

	@GetMapping("/productos/{id}")
	public ResponseEntity<String> producto(@PathVariable Long id) {
		return productosClient.obtener(id);
	}

	@PostMapping("/productos")
	public ResponseEntity<String> crearProducto(@RequestBody String cuerpo) {
		return productosClient.crear(cuerpo);
	}

	@PutMapping("/productos/{id}")
	public ResponseEntity<String> actualizarProducto(
			@PathVariable Long id,
			@RequestBody String cuerpo,
			Authentication autenticacion) {
		return productosClient.actualizar(id, cuerpoPara(autenticacion, cuerpo));
	}

	@DeleteMapping("/productos/{id}")
	public ResponseEntity<String> desactivarProducto(@PathVariable Long id) {
		return productosClient.desactivar(id);
	}

	@GetMapping("/categorias")
	public ResponseEntity<String> categorias() {
		return productosClient.listarCategorias();
	}

	@GetMapping("/categorias/{id}")
	public ResponseEntity<String> categoria(@PathVariable Long id) {
		return productosClient.obtenerCategoria(id);
	}

	@PostMapping("/categorias")
	public ResponseEntity<String> crearCategoria(@RequestBody String cuerpo) {
		return productosClient.crearCategoria(cuerpo);
	}

	@PutMapping("/categorias/{id}")
	public ResponseEntity<String> actualizarCategoria(@PathVariable Long id, @RequestBody String cuerpo) {
		return productosClient.actualizarCategoria(id, cuerpo);
	}

	@DeleteMapping("/categorias/{id}")
	public ResponseEntity<String> eliminarCategoria(@PathVariable Long id) {
		return productosClient.eliminarCategoria(id);
	}

	private String cuerpoPara(Authentication autenticacion, String cuerpo) {
		if (esAdmin(autenticacion)) {
			return cuerpo;
		}
		JsonNode json = objectMapper.readTree(cuerpo);
		if (json instanceof ObjectNode objeto) {
			objeto.remove("activo");
			return objectMapper.writeValueAsString(objeto);
		}
		return cuerpo;
	}

	private boolean esAdmin(Authentication autenticacion) {
		for (GrantedAuthority autoridad : autenticacion.getAuthorities()) {
			if ("ROLE_ADMIN".equals(autoridad.getAuthority())) {
				return true;
			}
		}
		return false;
	}

}
