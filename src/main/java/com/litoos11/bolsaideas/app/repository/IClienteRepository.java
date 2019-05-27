package com.litoos11.bolsaideas.app.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;

@Repository("clienteRepository")
public interface IClienteRepository extends JpaRepository<ClienteEntity, Serializable> {

	public abstract ClienteEntity findById(Long id);
}
