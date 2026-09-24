package com.mercaditocloud.bff.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mercaditocloud.bff.client.ProductosClient;
import com.mercaditocloud.bff.exception.ResourceNotFoundException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class CatalogoService {

	private final ProductosClient productosClient;
	private final ObjectMapper objectMapper;

	public CatalogoService(ProductosClient productosClient, ObjectMapper objectMapper) {
		this.productosClient = productosClient;
		this.objectMapper = objectMapper;
	}

	public ResponseEntity<String> producto(Long id) {
		ResponseEntity<String> respuesta = productosClient.obtener(id);
		if (!respuesta.getStatusCode().is2xxSuccessful() || respuesta.getBody() == null) {
			return respuesta;
		}
		JsonNode json = objectMapper.readTree(respuesta.getBody());
		JsonNode activo = json.get("activo");
		if (activo != null && activo.isBoolean() && !activo.asBoolean()) {
			throw new ResourceNotFoundException("Producto no encontrado");
		}
		return respuesta;
	}

}
