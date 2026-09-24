package com.mercaditocloud.carrito.dto;

import java.math.BigDecimal;

public record CarritoItemResponse(
		Long id,
		Long productoId,
		String nombre,
		Integer cantidad,
		BigDecimal precioUnitario,
		BigDecimal subtotal) {
}
