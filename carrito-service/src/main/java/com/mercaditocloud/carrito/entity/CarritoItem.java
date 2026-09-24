package com.mercaditocloud.carrito.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "carrito_item",
		uniqueConstraints = @UniqueConstraint(columnNames = {"carrito_id", "producto_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CarritoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "carrito_id", nullable = false)
	private Carrito carrito;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioUnitario;

}
