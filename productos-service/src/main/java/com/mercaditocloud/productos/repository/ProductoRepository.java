package com.mercaditocloud.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mercaditocloud.productos.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

	long countByCategoriaId(Long categoriaId);

}
