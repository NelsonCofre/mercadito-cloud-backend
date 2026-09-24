package com.mercaditocloud.productos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.productos.dto.ProductoRequest;
import com.mercaditocloud.productos.dto.ProductoResponse;
import com.mercaditocloud.productos.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

	private final ProductoService productoService;

	@PostMapping
	public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
	}

	@GetMapping
	public List<ProductoResponse> obtener(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) Boolean activo) {
		return productoService.obtener(q, categoriaId, activo);
	}

	@GetMapping("/{id}")
	public ProductoResponse obtenerPorId(@PathVariable Long id) {
		return productoService.obtenerPorId(id);
	}

	@PutMapping("/{id}")
	public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
		return productoService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ProductoResponse desactivar(@PathVariable Long id) {
		return productoService.desactivar(id);
	}

}
