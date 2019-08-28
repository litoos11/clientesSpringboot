package com.litoos11.bolsaideas.app.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;

@Repository("clienteRepository")
public interface IClienteRepository extends JpaRepository<ClienteEntity, Serializable> {

	//public abstract ClienteEntity findById(Long id);
	@Query("SELECT c FROM ClienteEntity c LEFT JOIN FETCH c.facturas f WHERE c.id = ?1")
	public abstract ClienteEntity fetchClienteByIdWithFacturas(Long id);
}
