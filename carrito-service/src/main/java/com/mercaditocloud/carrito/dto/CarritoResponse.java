package com.mercaditocloud.carrito.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CarritoResponse(
		Long id,
		String usuarioId,
		Instant fechaCreacion,
		Instant fechaActualizacion,
		List<CarritoItemResponse> items,
		BigDecimal total) {
}
