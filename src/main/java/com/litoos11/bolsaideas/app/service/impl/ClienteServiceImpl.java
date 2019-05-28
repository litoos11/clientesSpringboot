package com.litoos11.bolsaideas.app.service.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.repository.IClienteRepository;
import com.litoos11.bolsaideas.app.service.IClienteService;

@Service("clienteServiceImpl")
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	@Qualifier("clienteRepository")
	private IClienteRepository clienteRepository;

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

}
