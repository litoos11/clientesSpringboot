package com.litoos11.bolsaideas.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;

public interface IClienteService {
	
	public List<ClienteEntity> findAll();
	
	public Page<ClienteEntity> findAll(Pageable pageable);
	
	public ClienteEntity save(ClienteEntity cliente);
	
	public ClienteEntity findById(Long id); 
	
	public void deleteById(Long id);
}
