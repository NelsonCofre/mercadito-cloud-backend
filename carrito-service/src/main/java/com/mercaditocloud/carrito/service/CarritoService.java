package com.mercaditocloud.carrito.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercaditocloud.carrito.client.ProductoClient;
import com.mercaditocloud.carrito.client.ProductoRemoto;
import com.mercaditocloud.carrito.dto.CarritoItemResponse;
import com.mercaditocloud.carrito.dto.CarritoResponse;
import com.mercaditocloud.carrito.entity.Carrito;
import com.mercaditocloud.carrito.entity.CarritoItem;
import com.mercaditocloud.carrito.exception.ConflictoNegocioException;
import com.mercaditocloud.carrito.exception.ProductoServiceNoDisponibleException;
import com.mercaditocloud.carrito.exception.ResourceNotFoundException;
import com.mercaditocloud.carrito.repository.CarritoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

	private static final int CANTIDAD_MAXIMA = 999;

	private final CarritoRepository carritoRepository;
	private final ProductoClient productoClient;

	@Transactional
	public CarritoResponse obtener(String usuarioId) {
		return responder(obtenerOCrear(usuarioId));
	}

	@Transactional
	public CarritoResponse agregar(String usuarioId, Long productoId, int cantidad) {
		ProductoRemoto producto = productoDisponible(productoId);
		Carrito carrito = obtenerOCrear(usuarioId);
		CarritoItem item = buscarItem(carrito, productoId).orElseGet(() -> nuevoItem(carrito, productoId));
		int nuevaCantidad = item.getCantidad() == null ? cantidad : item.getCantidad() + cantidad;
		if (nuevaCantidad > CANTIDAD_MAXIMA) {
			throw new ConflictoNegocioException("La cantidad máxima por producto es " + CANTIDAD_MAXIMA);
		}
		item.setCantidad(nuevaCantidad);
		item.setPrecioUnitario(precio(producto));
		carrito.setFechaActualizacion(Instant.now());
		return responder(carritoRepository.save(carrito));
	}

	@Transactional
	public CarritoResponse actualizarCantidad(String usuarioId, Long productoId, int cantidad) {
		ProductoRemoto producto = productoDisponible(productoId);
		Carrito carrito = obtenerExistente(usuarioId);
		CarritoItem item = buscarItem(carrito, productoId)
				.orElseThrow(() -> new ResourceNotFoundException("El producto no está en el carrito"));
		item.setCantidad(cantidad);
		item.setPrecioUnitario(precio(producto));
		carrito.setFechaActualizacion(Instant.now());
		return responder(carrito);
	}

	@Transactional
	public CarritoResponse eliminarItem(String usuarioId, Long productoId) {
		Carrito carrito = obtenerExistente(usuarioId);
		CarritoItem item = buscarItem(carrito, productoId)
				.orElseThrow(() -> new ResourceNotFoundException("El producto no está en el carrito"));
		carrito.getItems().remove(item);
		carrito.setFechaActualizacion(Instant.now());
		return responder(carrito);
	}

	@Transactional
	public CarritoResponse vaciar(String usuarioId) {
		Carrito carrito = obtenerExistente(usuarioId);
		carrito.getItems().clear();
		carrito.setFechaActualizacion(Instant.now());
		return responder(carrito);
	}

	private Carrito obtenerOCrear(String usuarioId) {
		return carritoRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
			Instant ahora = Instant.now();
			Carrito carrito = new Carrito();
			carrito.setUsuarioId(usuarioId);
			carrito.setFechaCreacion(ahora);
			carrito.setFechaActualizacion(ahora);
			return carritoRepository.save(carrito);
		});
	}

	private Carrito obtenerExistente(String usuarioId) {
		return carritoRepository.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
	}

	private ProductoRemoto productoDisponible(Long productoId) {
		ProductoRemoto producto = productoClient.obtenerPorId(productoId);
		if (!producto.activo()) {
			throw new ConflictoNegocioException("El producto no está disponible");
		}
		return producto;
	}

	private Optional<CarritoItem> buscarItem(Carrito carrito, Long productoId) {
		return carrito.getItems().stream()
				.filter(item -> item.getProductoId().equals(productoId))
				.findFirst();
	}

	private CarritoItem nuevoItem(Carrito carrito, Long productoId) {
		CarritoItem item = new CarritoItem();
		item.setCarrito(carrito);
		item.setProductoId(productoId);
		item.setCantidad(0);
		carrito.getItems().add(item);
		return item;
	}

	private BigDecimal precio(ProductoRemoto producto) {
		return producto.precio().setScale(2, RoundingMode.HALF_UP);
	}

	private CarritoResponse responder(Carrito carrito) {
		List<CarritoItemResponse> items = carrito.getItems().stream()
				.sorted(Comparator.comparing(CarritoItem::getProductoId))
				.map(this::aItem)
				.toList();
		BigDecimal total = items.stream()
				.map(CarritoItemResponse::subtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return new CarritoResponse(
				carrito.getId(),
				carrito.getUsuarioId(),
				carrito.getFechaCreacion(),
				carrito.getFechaActualizacion(),
				items,
				total);
	}

	private CarritoItemResponse aItem(CarritoItem item) {
		BigDecimal subtotal = item.getPrecioUnitario()
				.multiply(BigDecimal.valueOf(item.getCantidad()))
				.setScale(2, RoundingMode.HALF_UP);
		return new CarritoItemResponse(
				item.getId(),
				item.getProductoId(),
				nombreProducto(item.getProductoId()),
				item.getCantidad(),
				item.getPrecioUnitario(),
				subtotal);
	}

	private String nombreProducto(Long productoId) {
		try {
			ProductoRemoto producto = productoClient.obtenerPorId(productoId);
			return producto == null ? null : producto.nombre();
		} catch (ResourceNotFoundException | ProductoServiceNoDisponibleException ex) {
			return null;
		}
	}

}
