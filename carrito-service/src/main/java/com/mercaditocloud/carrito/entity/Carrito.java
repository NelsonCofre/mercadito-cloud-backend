package com.mercaditocloud.carrito.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carrito")
@Getter
@Setter
@NoArgsConstructor
public class Carrito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false, unique = true, length = 128)
	private String usuarioId;

	@Column(name = "fecha_creacion", nullable = false)
	private Instant fechaCreacion;

	@Column(name = "fecha_actualizacion", nullable = false)
	private Instant fechaActualizacion;

	@OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CarritoItem> items = new ArrayList<>();

}
