package com.mercaditocloud.productos.dto;

import com.mercaditocloud.productos.entity.Categoria;

public record CategoriaResponse(Long id, String nombre, String descripcion) {

	public static CategoriaResponse from(Categoria categoria) {
		return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
	}

}
