package com.litoos11.bolsaideas.app.service;

import java.util.List;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;

public interface IClienteService {
	
	public List<ClienteEntity> findAll();
	
	public ClienteEntity save(ClienteEntity cliente);
	
	public ClienteEntity findById(Long id); 
}
