package com.mercaditocloud.bff.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CognitoJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Collection<GrantedAuthority> roles = new ArrayList<>();
		for (String grupo : grupos(jwt)) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + grupo));
		}
		return new JwtAuthenticationToken(jwt, roles, jwt.getSubject());
	}

	private List<String> grupos(Jwt jwt) {
		Object valor = jwt.getClaim("cognito:groups");
		if (valor instanceof Collection<?> coleccion) {
			return coleccion.stream().map(String::valueOf).toList();
		}
		if (valor instanceof String texto && !texto.isBlank()) {
			return List.of(texto);
		}
		return List.of();
	}

}
