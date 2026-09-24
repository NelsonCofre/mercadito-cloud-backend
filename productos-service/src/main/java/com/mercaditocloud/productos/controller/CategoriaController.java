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
import org.springframework.web.bind.annotation.RestController;

import com.mercaditocloud.productos.dto.CategoriaRequest;
import com.mercaditocloud.productos.dto.CategoriaResponse;
import com.mercaditocloud.productos.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService categoriaService;

	@PostMapping
	public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(request));
	}

	@GetMapping
	public List<CategoriaResponse> obtenerTodas() {
		return categoriaService.obtenerTodas();
	}

	@GetMapping("/{id}")
	public CategoriaResponse obtenerPorId(@PathVariable Long id) {
		return categoriaService.obtenerPorId(id);
	}

	@PutMapping("/{id}")
	public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
		return categoriaService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		categoriaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
