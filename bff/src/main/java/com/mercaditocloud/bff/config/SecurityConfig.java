package com.mercaditocloud.bff.config;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

	private final CognitoJwtConverter cognitoJwtConverter;

	public SecurityConfig(CognitoJwtConverter cognitoJwtConverter) {
		this.cognitoJwtConverter = cognitoJwtConverter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/catalogo/**").hasRole("CLIENTE")
						.requestMatchers(HttpMethod.GET, "/api/admin/productos", "/api/admin/productos/**")
						.hasAnyRole("VENDEDOR", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/admin/productos/**").hasAnyRole("VENDEDOR", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/admin/categorias", "/api/admin/categorias/**")
						.hasAnyRole("VENDEDOR", "ADMIN")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/carrito/**").hasRole("CLIENTE")
						.anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(cognitoJwtConverter))
						.authenticationEntryPoint((request, response, ex) -> escribir(response, 401, "Debes iniciar sesión"))
						.accessDeniedHandler((request, response, ex) -> escribir(response, 403, "No tienes permiso para esta acción")))
				.exceptionHandling(errores -> errores
						.authenticationEntryPoint((request, response, ex) -> escribir(response, 401, "Debes iniciar sesión"))
						.accessDeniedHandler((request, response, ex) -> escribir(response, 403, "No tienes permiso para esta acción")));
		return http.build();
	}

	@Bean
	@Profile("!test")
	JwtDecoder jwtDecoder(
			@Value("${cognito.issuer-uri}") String issuer,
			@Value("${cognito.client-id}") String clientId) {
		NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuer);
		OAuth2TokenValidator<Jwt> audiencia = new JwtClaimValidator<>("aud", valor -> audienciaValida(valor, clientId));
		OAuth2TokenValidator<Jwt> tipo = new JwtClaimValidator<>("token_use", "id"::equals);
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(issuer),
				audiencia,
				tipo));
		return decoder;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuracion = new CorsConfiguration();
		configuracion.setAllowedOriginPatterns(List.of("*"));
		configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuracion.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuracion);
		return source;
	}

	private boolean audienciaValida(Object valor, String clientId) {
		if (valor instanceof String texto) {
			return clientId.equals(texto);
		}
		if (valor instanceof Collection<?> coleccion) {
			return coleccion.contains(clientId);
		}
		return false;
	}

	private void escribir(jakarta.servlet.http.HttpServletResponse response, int status, String mensaje) throws java.io.IOException {
		response.setStatus(status);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write("{\"status\":" + status + ",\"mensaje\":\"" + mensaje + "\",\"detalles\":[]}");
	}

}
