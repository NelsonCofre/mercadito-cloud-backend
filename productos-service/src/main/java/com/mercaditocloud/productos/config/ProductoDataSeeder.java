package com.mercaditocloud.productos.config;

import java.math.BigDecimal;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mercaditocloud.productos.entity.Categoria;
import com.mercaditocloud.productos.entity.Producto;
import com.mercaditocloud.productos.repository.CategoriaRepository;
import com.mercaditocloud.productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ProductoDataSeeder implements ApplicationRunner {

	private final CategoriaRepository categoriaRepository;
	private final ProductoRepository productoRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (categoriaRepository.count() > 0) {
			return;
		}

		Categoria abarrotes = guardarCategoria("Abarrotes", "Productos de despensa");
		Categoria lacteos = guardarCategoria("Lácteos", "Leches y derivados");
		Categoria bebidas = guardarCategoria("Bebidas", "Bebidas y jugos");

		guardarProducto("Arroz grado 1, 1 kg", "Arroz para cocinar", "1290.00", 40, abarrotes);
		guardarProducto("Leche entera 1 L", "Leche fresca", "1190.00", 30, lacteos);
		guardarProducto("Agua mineral 1.5 L", "Agua sin gas", "790.00", 50, bebidas);
	}

	private Categoria guardarCategoria(String nombre, String descripcion) {
		Categoria categoria = new Categoria();
		categoria.setNombre(nombre);
		categoria.setDescripcion(descripcion);
		return categoriaRepository.save(categoria);
	}

	private void guardarProducto(String nombre, String descripcion, String precio, int stock, Categoria categoria) {
		Producto producto = new Producto();
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setPrecio(new BigDecimal(precio));
		producto.setStock(stock);
		producto.setCategoria(categoria);
		producto.setActivo(true);
		productoRepository.save(producto);
	}

}
