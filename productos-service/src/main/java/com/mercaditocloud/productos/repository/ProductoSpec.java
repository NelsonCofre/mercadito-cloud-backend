package com.mercaditocloud.productos.repository;

import org.springframework.data.jpa.domain.Specification;

import com.mercaditocloud.productos.entity.Producto;

import jakarta.persistence.criteria.Predicate;

public final class ProductoSpec {

	private ProductoSpec() {
	}

	public static Specification<Producto> filtrar(String q, Long categoriaId, Boolean activo) {
		return (root, query, cb) -> {
			Predicate filtro = cb.conjunction();
			if (q != null) {
				String patron = "%" + q.toLowerCase() + "%";
				filtro = cb.and(filtro, cb.or(
						cb.like(cb.lower(root.get("nombre")), patron),
						cb.like(cb.lower(cb.coalesce(root.get("descripcion"), "")), patron)));
			}
			if (categoriaId != null) {
				filtro = cb.and(filtro, cb.equal(root.get("categoria").get("id"), categoriaId));
			}
			if (activo != null) {
				filtro = cb.and(filtro, cb.equal(root.get("activo"), activo));
			}
			return filtro;
		};
	}

}
