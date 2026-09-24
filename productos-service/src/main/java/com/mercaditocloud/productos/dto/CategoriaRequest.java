package com.mercaditocloud.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 120, message = "El nombre admite hasta 120 caracteres")
		String nombre,

		@Size(max = 500, message = "La descripción admite hasta 500 caracteres")
		String descripcion) {
}
