package com.mercaditocloud.productos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 120, message = "El nombre admite hasta 120 caracteres")
		String nombre,

		@Size(max = 1000, message = "La descripción admite hasta 1000 caracteres")
		String descripcion,

		@NotNull(message = "El precio es obligatorio")
		@DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
		@Digits(integer = 10, fraction = 2, message = "El precio admite hasta 2 decimales")
		BigDecimal precio,

		@NotNull(message = "El stock es obligatorio")
		@PositiveOrZero(message = "El stock no puede ser negativo")
		Integer stock,

		@NotNull(message = "La categoría es obligatoria")
		Long categoriaId,

		Boolean activo) {
}
