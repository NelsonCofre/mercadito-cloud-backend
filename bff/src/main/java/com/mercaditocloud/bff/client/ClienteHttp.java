package com.mercaditocloud.bff.client;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import com.mercaditocloud.bff.exception.ServicioNoDisponibleException;

@Component
public class ClienteHttp {

	public ResponseEntity<String> ejecutar(
			RestClient cliente,
			HttpMethod metodo,
			Function<UriBuilder, URI> uri,
			String cuerpo,
			String usuarioId,
			String nombreServicio) {
		try {
			RestClient.RequestBodySpec solicitud = cliente.method(metodo)
					.uri(uri)
					.headers(headers -> {
						if (usuarioId != null) {
							headers.set("X-Usuario-Id", usuarioId);
						}
					});
			if (cuerpo != null) {
				solicitud = solicitud.contentType(MediaType.APPLICATION_JSON).body(cuerpo);
			}
			return solicitud.exchange(this::leer);
		} catch (RestClientException ex) {
			throw new ServicioNoDisponibleException(nombreServicio + " no está disponible");
		}
	}

	private ResponseEntity<String> leer(
			HttpRequest request,
			RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
		String contenido = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusCode());
		MediaType tipo = response.getHeaders().getContentType();
		if (tipo != null) {
			builder.contentType(tipo);
		}
		if (contenido.isEmpty()) {
			return builder.build();
		}
		return builder.body(contenido);
	}

}
