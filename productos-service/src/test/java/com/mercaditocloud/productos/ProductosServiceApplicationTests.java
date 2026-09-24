package com.mercaditocloud.productos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductosServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void gestionaCategoriasYProductos() throws Exception {
		long abarrotesId = crearCategoria("Abarrotes", "Despensa");
		long bebidasId = crearCategoria("Bebidas", "Bebidas y jugos");

		long arrozId = crearProducto("Arroz 1 kg", "1890.00", 10, abarrotesId);
		crearProducto("Agua mineral", "790.00", 20, bebidasId);

		mockMvc.perform(get("/api/productos").param("q", "arroz"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Arroz 1 kg"))
				.andExpect(jsonPath("$[0].categoriaNombre").value("Abarrotes"));

		mockMvc.perform(put("/api/productos/{id}", arrozId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(productoJson("Arroz grado 1", "1990.00", 8, abarrotesId, true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(1990.00))
				.andExpect(jsonPath("$.stock").value(8));

		mockMvc.perform(delete("/api/productos/{id}", arrozId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activo").value(false));

		mockMvc.perform(get("/api/productos").param("activo", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Agua mineral"));

		mockMvc.perform(get("/api/productos/{id}", arrozId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activo").value(false));

		mockMvc.perform(delete("/api/categorias/{id}", abarrotesId))
				.andExpect(status().isConflict());

		long vaciaId = crearCategoria("Snacks", "Para picar");
		mockMvc.perform(delete("/api/categorias/{id}", vaciaId))
				.andExpect(status().isNoContent());
	}

	@Test
	void rechazaDatosInvalidosYNombresDuplicados() throws Exception {
		mockMvc.perform(post("/api/categorias")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"\"}"))
				.andExpect(status().isBadRequest());

		crearCategoria("Lácteos", "Leches");

		mockMvc.perform(post("/api/categorias")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nombre\":\"lácteos\"}"))
				.andExpect(status().isConflict());

		mockMvc.perform(get("/api/productos/999"))
				.andExpect(status().isNotFound());
	}

	private long crearCategoria(String nombre, String descripcion) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/categorias")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"%s","descripcion":"%s"}
								""".formatted(nombre, descripcion)))
				.andExpect(status().isCreated())
				.andReturn();
		return leerId(result);
	}

	private long crearProducto(String nombre, String precio, int stock, long categoriaId) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/productos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productoJson(nombre, precio, stock, categoriaId, null)))
				.andExpect(status().isCreated())
				.andReturn();
		return leerId(result);
	}

	private String productoJson(String nombre, String precio, int stock, long categoriaId, Boolean activo) {
		String activoJson = activo == null ? "" : ",\"activo\":" + activo;
		return """
				{"nombre":"%s","precio":%s,"stock":%d,"categoriaId":%d%s}
				""".formatted(nombre, precio, stock, categoriaId, activoJson);
	}

	private long leerId(MvcResult result) throws Exception {
		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		return json.get("id").asLong();
	}

}
