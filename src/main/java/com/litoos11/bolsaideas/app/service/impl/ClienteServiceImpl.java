package com.litoos11.bolsaideas.app.service.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.entity.FacturaEntity;
import com.litoos11.bolsaideas.app.entity.ProductoEntity;
import com.litoos11.bolsaideas.app.repository.IClienteRepository;
import com.litoos11.bolsaideas.app.repository.IFacturaRepository;
import com.litoos11.bolsaideas.app.repository.IProductoRepository;
import com.litoos11.bolsaideas.app.service.IClienteService;

@Service("clienteServiceImpl")
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	@Qualifier("clienteRepository")
	private IClienteRepository clienteRepository;
	
	@Autowired
	@Qualifier("productoRepository")
	private IProductoRepository productoRepository;
	
	@Autowired
	@Qualifier("facturaRepository")
	private IFacturaRepository facturaRepository;
	
	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public ClienteEntity findById(Long id) {
		return clienteRepository.findById(id).orElse(null) ;
		// return em.find(ClienteEntity.class, id);
	}

	//@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<ClienteEntity> findAll() {
		return clienteRepository.findAll();
		//return em.createQuery("from ClienteEntity").getResultList();
	}

	@Override
	@Transactional
	public ClienteEntity save(ClienteEntity cliente) {
		return clienteRepository.save(cliente);
		/*
		 * if(cliente.getId() != null && cliente.getId() > 0) { em.merge(cliente); }else
		 * { em.persist(cliente); }
		 */

	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		ClienteEntity cliente = findById(id);
		if (null != cliente) {
			clienteRepository.delete(cliente);
			// em.remove(cliente);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ClienteEntity> findAll(Pageable pageable) {
		return clienteRepository.findAll(pageable);
	}

	@Override
	public List<ProductoEntity> findByNombre(String term) {		
		return productoRepository.findByNombreLikeIgnoreCase("%"+term+"%");
	}

	@Override
	@Transactional
	public void saveFactura(FacturaEntity factura) {
		facturaRepository.save(factura);
		
	}

	@Override
	@Transactional(readOnly=true)
	public ProductoEntity findProductoById(Long id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly=true)
	public FacturaEntity findFacturaById(Long id) {
		// TODO Auto-generated method stub
		return facturaRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteFacturaById(Long id) {
		// TODO Auto-generated method stub
		facturaRepository.deleteById(id);
	}

	@Override
	public FacturaEntity fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id) {
		// TODO Auto-generated method stub
		return facturaRepository.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id);
	}

	@Override
	public ClienteEntity fetchClienteByIdWithFacturas(Long id) {
		// TODO Auto-generated method stub
		return clienteRepository.fetchClienteByIdWithFacturas(id);
	}

}
