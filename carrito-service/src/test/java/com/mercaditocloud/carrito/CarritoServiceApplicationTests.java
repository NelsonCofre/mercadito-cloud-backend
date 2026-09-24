package com.mercaditocloud.carrito;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercaditocloud.carrito.client.ProductoClient;
import com.mercaditocloud.carrito.client.ProductoRemoto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarritoServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductoClient productoClient;

	@Test
	void contextLoads() {
	}

	@Test
	void gestionaElCarritoDelUsuario() throws Exception {
		when(productoClient.obtenerPorId(10L))
				.thenReturn(new ProductoRemoto(10L, "Arroz", new BigDecimal("1000.00"), true));

		mockMvc.perform(get("/api/carrito").header("X-Usuario-Id", "cliente-flujo"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usuarioId").value("cliente-flujo"))
				.andExpect(jsonPath("$.items.length()").value(0))
				.andExpect(jsonPath("$.total").value(0));

		mockMvc.perform(post("/api/carrito/items")
						.header("X-Usuario-Id", "cliente-flujo")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"productoId\":10,\"cantidad\":2}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].nombre").value("Arroz"))
				.andExpect(jsonPath("$.items[0].cantidad").value(2))
				.andExpect(jsonPath("$.items[0].precioUnitario").value(1000.00))
				.andExpect(jsonPath("$.total").value(2000.00));

		mockMvc.perform(post("/api/carrito/items")
						.header("X-Usuario-Id", "cliente-flujo")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"productoId\":10,\"cantidad\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].cantidad").value(3))
				.andExpect(jsonPath("$.total").value(3000.00));

		mockMvc.perform(put("/api/carrito/items/10")
						.header("X-Usuario-Id", "cliente-flujo")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"cantidad\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].cantidad").value(1))
				.andExpect(jsonPath("$.total").value(1000.00));

		mockMvc.perform(delete("/api/carrito/items/10").header("X-Usuario-Id", "cliente-flujo"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(0))
				.andExpect(jsonPath("$.total").value(0));
	}

	@Test
	void vaciaElCarritoYRechazaProductoInactivo() throws Exception {
		when(productoClient.obtenerPorId(20L))
				.thenReturn(new ProductoRemoto(20L, "Agua", new BigDecimal("790.00"), true));
		when(productoClient.obtenerPorId(21L))
				.thenReturn(new ProductoRemoto(21L, "Leche", new BigDecimal("1190.00"), false));

		mockMvc.perform(post("/api/carrito/items")
						.header("X-Usuario-Id", "cliente-vacio")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"productoId\":20,\"cantidad\":1}"))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/api/carrito").header("X-Usuario-Id", "cliente-vacio"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items.length()").value(0));

		mockMvc.perform(post("/api/carrito/items")
						.header("X-Usuario-Id", "cliente-vacio")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"productoId\":21,\"cantidad\":1}"))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/api/carrito/items")
						.header("X-Usuario-Id", " ")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"productoId\":20,\"cantidad\":1}"))
				.andExpect(status().isBadRequest());
	}

}
