package com.mercaditocloud.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.bff.client.ProductosClient;
import com.mercaditocloud.bff.service.CatalogoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

	private final ProductosClient productosClient;
	private final CatalogoService catalogoService;

	@GetMapping("/productos")
	public ResponseEntity<String> productos(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Long categoriaId) {
		return productosClient.listar(q, categoriaId, true);
	}

	@GetMapping("/productos/{id}")
	public ResponseEntity<String> producto(@PathVariable Long id) {
		return catalogoService.producto(id);
	}

	@GetMapping("/categorias")
	public ResponseEntity<String> categorias() {
		return productosClient.listarCategorias();
	}

}
