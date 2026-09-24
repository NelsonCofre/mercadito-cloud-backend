package com.mercaditocloud.carrito.client;

import java.math.BigDecimal;

public record ProductoRemoto(Long id, String nombre, BigDecimal precio, boolean activo) {
}
