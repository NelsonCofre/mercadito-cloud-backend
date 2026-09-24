package com.mercaditocloud.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@Profile("test")
public class JwtDecoderDePrueba {

	@Bean
	JwtDecoder jwtDecoder() {
		return token -> Jwt.withTokenValue(token).header("alg", "none").subject("test").build();
	}

}
