package com.mercaditocloud.productos.service;

import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercaditocloud.productos.dto.ProductoRequest;
import com.mercaditocloud.productos.dto.ProductoResponse;
import com.mercaditocloud.productos.entity.Producto;
import com.mercaditocloud.productos.exception.ResourceNotFoundException;
import com.mercaditocloud.productos.repository.ProductoRepository;
import com.mercaditocloud.productos.repository.ProductoSpec;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final CategoriaService categoriaService;

	@Transactional
	public ProductoResponse crear(ProductoRequest request) {
		Producto producto = new Producto();
		aplicar(producto, request, true);
		return ProductoResponse.from(productoRepository.save(producto));
	}

	@Transactional(readOnly = true)
	public List<ProductoResponse> obtener(String q, Long categoriaId, Boolean activo) {
		return productoRepository.findAll(ProductoSpec.filtrar(texto(q), categoriaId, activo), Sort.by("nombre")).stream()
				.map(ProductoResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductoResponse obtenerPorId(Long id) {
		return ProductoResponse.from(obtenerEntidad(id));
	}

	@Transactional
	public ProductoResponse actualizar(Long id, ProductoRequest request) {
		Producto producto = obtenerEntidad(id);
		aplicar(producto, request, false);
		return ProductoResponse.from(producto);
	}

	@Transactional
	public ProductoResponse desactivar(Long id) {
		Producto producto = obtenerEntidad(id);
		producto.setActivo(false);
		return ProductoResponse.from(producto);
	}

	private Producto obtenerEntidad(Long id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
	}

	private void aplicar(Producto producto, ProductoRequest request, boolean creacion) {
		producto.setNombre(request.nombre().trim());
		producto.setDescripcion(limpiar(request.descripcion()));
		producto.setPrecio(request.precio().setScale(2, RoundingMode.HALF_UP));
		producto.setStock(request.stock());
		producto.setCategoria(categoriaService.obtenerEntidad(request.categoriaId()));
		if (request.activo() != null) {
			producto.setActivo(request.activo());
		} else if (creacion) {
			producto.setActivo(true);
		}
	}

	private String texto(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

	private String limpiar(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return valor.trim();
	}

}
