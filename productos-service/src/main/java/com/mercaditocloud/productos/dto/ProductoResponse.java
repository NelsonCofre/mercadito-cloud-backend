package com.mercaditocloud.productos.dto;

import java.math.BigDecimal;

import com.mercaditocloud.productos.entity.Producto;

public record ProductoResponse(
		Long id,
		String nombre,
		String descripcion,
		BigDecimal precio,
		Integer stock,
		Long categoriaId,
		String categoriaNombre,
		boolean activo) {

	public static ProductoResponse from(Producto producto) {
		return new ProductoResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getDescripcion(),
				producto.getPrecio(),
				producto.getStock(),
				producto.getCategoria().getId(),
				producto.getCategoria().getNombre(),
				producto.isActivo());
	}

}
