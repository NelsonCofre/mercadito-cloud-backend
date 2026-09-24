package com.mercaditocloud.carrito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mercaditocloud.carrito.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

	@EntityGraph(attributePaths = "items")
	Optional<Carrito> findByUsuarioId(String usuarioId);

}
