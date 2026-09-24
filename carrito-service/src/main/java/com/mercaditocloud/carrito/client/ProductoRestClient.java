package com.mercaditocloud.carrito.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.mercaditocloud.carrito.exception.ProductoServiceNoDisponibleException;
import com.mercaditocloud.carrito.exception.ResourceNotFoundException;

@Component
public class ProductoRestClient implements ProductoClient {

	private final RestClient restClient;

	public ProductoRestClient(@Value("${productos.service.url}") String productosServiceUrl) {
		this.restClient = RestClient.builder().baseUrl(productosServiceUrl).build();
	}

	@Override
	public ProductoRemoto obtenerPorId(Long productoId) {
		try {
			ProductoRemoto producto = restClient.get()
					.uri("/api/productos/{id}", productoId)
					.retrieve()
					.body(ProductoRemoto.class);
			if (producto == null) {
				throw new ResourceNotFoundException("Producto no encontrado");
			}
			return producto;
		} catch (HttpClientErrorException.NotFound ex) {
			throw new ResourceNotFoundException("Producto no encontrado");
		} catch (RestClientException ex) {
			throw new ProductoServiceNoDisponibleException("productos-service no está disponible");
		}
	}

}
