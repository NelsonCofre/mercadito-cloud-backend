package com.mercaditocloud.bff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.mercaditocloud.bff.client.CarritoClient;
import com.mercaditocloud.bff.client.ProductosClient;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BffApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductosClient productosClient;

	@MockitoBean
	private CarritoClient carritoClient;

	@Test
	void contextLoads() {
	}

	@Test
	void elListadoDeAdminReenviaElJsonDeProductos() throws Exception {
		when(productosClient.listar(null, null, null))
				.thenReturn(ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body("[{\"id\":1,\"nombre\":\"Arroz\"}]"));

		mockMvc.perform(get("/api/admin/productos").with(como("ADMIN", "admin-1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Arroz"));
	}

	@Test
	void elCatalogoSoloPideProductosActivos() throws Exception {
		when(productosClient.listar("arroz", null, true))
				.thenReturn(ResponseEntity.ok("[{\"id\":1,\"nombre\":\"Arroz\",\"activo\":true}]"));

		mockMvc.perform(get("/api/catalogo/productos").param("q", "arroz").with(como("CLIENTE", "cliente-1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Arroz"));

		verify(productosClient).listar("arroz", null, true);
	}

	@Test
	void elCatalogoOcultaUnProductoInactivo() throws Exception {
		when(productosClient.obtener(5L))
				.thenReturn(ResponseEntity.ok("{\"id\":5,\"nombre\":\"Arroz\",\"activo\":false}"));

		mockMvc.perform(get("/api/catalogo/productos/5").with(como("CLIENTE", "cliente-1")))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value("Producto no encontrado"));
	}

	@Test
	void laAdministracionReenviaElAltaDeUnProducto() throws Exception {
		String cuerpo = "{\"nombre\":\"Pan\"}";
		when(productosClient.crear(cuerpo))
				.thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("{\"id\":3,\"nombre\":\"Pan\"}"));

		mockMvc.perform(post("/api/admin/productos")
						.with(como("ADMIN", "admin-1"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(cuerpo))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(3));
	}

	@Test
	void elVendedorNoPuedeCrearNiDesactivar() throws Exception {
		mockMvc.perform(post("/api/admin/productos")
						.with(como("VENDEDOR", "vendedor-1"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Pan\"}"))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/api/admin/productos/4").with(como("VENDEDOR", "vendedor-1")))
				.andExpect(status().isForbidden());
	}

	@Test
	void elVendedorEditaSinPoderDesactivar() throws Exception {
		when(productosClient.actualizar(eq(4L), org.mockito.ArgumentMatchers.contains("nombre")))
				.thenReturn(ResponseEntity.ok("{\"id\":4,\"activo\":true}"));

		mockMvc.perform(put("/api/admin/productos/4")
						.with(como("VENDEDOR", "vendedor-1"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"Pan\",\"activo\":false}"))
				.andExpect(status().isOk());

		ArgumentCaptor<String> cuerpo = ArgumentCaptor.forClass(String.class);
		verify(productosClient).actualizar(eq(4L), cuerpo.capture());
		assertThat(cuerpo.getValue()).doesNotContain("activo");
	}

	@Test
	void elCarritoUsaElUsuarioDelToken() throws Exception {
		String cuerpo = "{\"productoId\":10,\"cantidad\":2}";
		when(carritoClient.agregar("cliente-1", cuerpo))
				.thenReturn(ResponseEntity.ok("{\"usuarioId\":\"cliente-1\",\"total\":2000.00}"));

		mockMvc.perform(post("/api/carrito/items")
						.with(como("CLIENTE", "cliente-1"))
						.header("X-Usuario-Id", "otro-usuario")
						.contentType(MediaType.APPLICATION_JSON)
						.content(cuerpo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total").value(2000.00));

		mockMvc.perform(delete("/api/carrito"))
				.andExpect(status().isUnauthorized());
	}

	private RequestPostProcessor como(String rol, String usuarioId) {
		return jwt()
				.jwt(token -> token.subject(usuarioId))
				.authorities(new SimpleGrantedAuthority("ROLE_" + rol));
	}

}
