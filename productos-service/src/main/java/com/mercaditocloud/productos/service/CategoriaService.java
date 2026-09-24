package com.mercaditocloud.productos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercaditocloud.productos.dto.CategoriaRequest;
import com.mercaditocloud.productos.dto.CategoriaResponse;
import com.mercaditocloud.productos.entity.Categoria;
import com.mercaditocloud.productos.exception.ConflictoNegocioException;
import com.mercaditocloud.productos.exception.ResourceNotFoundException;
import com.mercaditocloud.productos.repository.CategoriaRepository;
import com.mercaditocloud.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final ProductoRepository productoRepository;

	@Transactional
	public CategoriaResponse crear(CategoriaRequest request) {
		validarNombreDisponible(request.nombre(), null);
		Categoria categoria = new Categoria();
		aplicar(categoria, request);
		return CategoriaResponse.from(categoriaRepository.save(categoria));
	}

	@Transactional(readOnly = true)
	public List<CategoriaResponse> obtenerTodas() {
		return categoriaRepository.findAll().stream()
				.map(CategoriaResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public CategoriaResponse obtenerPorId(Long id) {
		return CategoriaResponse.from(obtenerEntidad(id));
	}

	@Transactional
	public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
		Categoria categoria = obtenerEntidad(id);
		validarNombreDisponible(request.nombre(), id);
		aplicar(categoria, request);
		return CategoriaResponse.from(categoria);
	}

	@Transactional
	public void eliminar(Long id) {
		Categoria categoria = obtenerEntidad(id);
		if (productoRepository.countByCategoriaId(id) > 0) {
			throw new ConflictoNegocioException("No se puede eliminar la categoría porque tiene productos asociados");
		}
		categoriaRepository.delete(categoria);
	}

	public Categoria obtenerEntidad(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
	}

	private void aplicar(Categoria categoria, CategoriaRequest request) {
		categoria.setNombre(request.nombre().trim());
		categoria.setDescripcion(limpiar(request.descripcion()));
	}

	private void validarNombreDisponible(String nombre, Long idActual) {
		boolean ocupado = idActual == null
				? categoriaRepository.findByNombreIgnoreCase(nombre.trim()).isPresent()
				: categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre.trim(), idActual);
		if (ocupado) {
			throw new ConflictoNegocioException("Ya existe una categoría con ese nombre");
		}
	}

	private String limpiar(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

}
