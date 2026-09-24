package com.mercaditocloud.carrito.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarItemRequest(
		@NotNull(message = "El producto es obligatorio")
		Long productoId,

		@NotNull(message = "La cantidad es obligatoria")
		@Min(value = 1, message = "La cantidad mínima es 1")
		@Max(value = 999, message = "La cantidad máxima es 999")
		Integer cantidad) {
}
