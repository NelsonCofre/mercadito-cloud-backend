package com.mercaditocloud.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientesConfig {

	@Bean
	RestClient productosRestClient(@Value("${productos.service.url}") String url) {
		return RestClient.builder().baseUrl(url).build();
	}

	@Bean
	RestClient carritoRestClient(@Value("${carrito.service.url}") String url) {
		return RestClient.builder().baseUrl(url).build();
	}

}
