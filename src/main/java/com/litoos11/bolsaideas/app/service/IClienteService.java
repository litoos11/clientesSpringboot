package com.litoos11.bolsaideas.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.entity.FacturaEntity;
import com.litoos11.bolsaideas.app.entity.ProductoEntity;

public interface IClienteService {
	
	public List<ClienteEntity> findAll();
	
	public Page<ClienteEntity> findAll(Pageable pageable);
	
	public ClienteEntity save(ClienteEntity cliente);
	
	public ClienteEntity findById(Long id); 
	
	public void deleteById(Long id);
	
	public List<ProductoEntity> findByNombre(String term);
	
	public void saveFactura(FacturaEntity factura);
	
	public ProductoEntity findProductoById(Long id);
	
	public FacturaEntity findFacturaById(Long id);
	
	public void deleteFacturaById(Long id);
	
	public FacturaEntity fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id);
	
	public ClienteEntity fetchClienteByIdWithFacturas(Long id);
}
