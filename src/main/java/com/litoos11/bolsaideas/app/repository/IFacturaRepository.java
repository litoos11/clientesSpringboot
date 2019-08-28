package com.litoos11.bolsaideas.app.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.litoos11.bolsaideas.app.entity.FacturaEntity;

@Repository("facturaRepository")
public interface IFacturaRepository extends JpaRepository<FacturaEntity, Serializable> {

	@Query("SELECT f FROM FacturaEntity f JOIN FETCH f.cliente c JOIN FETCH f.items l JOIN FETCH l.producto WHERE f.id=?1")
	public abstract FacturaEntity fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id);
}
