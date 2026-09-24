package com.mercaditocloud.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mercaditocloud.productos.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	Optional<Categoria> findByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

}
