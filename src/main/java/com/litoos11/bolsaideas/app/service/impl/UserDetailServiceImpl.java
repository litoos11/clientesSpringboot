package com.litoos11.bolsaideas.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.litoos11.bolsaideas.app.controller.ClienteController;
import com.litoos11.bolsaideas.app.entity.RolEntity;
import com.litoos11.bolsaideas.app.entity.UsuarioEntity;
import com.litoos11.bolsaideas.app.repository.IUsuarioRepository;

@Service("userDetailServiceImpl")
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	@Qualifier("usuarioRepository")
	private IUsuarioRepository usuarioRepository;
	
	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Override
	//@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UsuarioEntity usuario = usuarioRepository.findByUsuario(user);
		
		if(usuario == null) {
			LOG.error("Error login: no existen el usuario ' " + user +"'");
			throw new UsernameNotFoundException("Usuario " + user  + " no existe en el sistema!");
		}

		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();

		for (RolEntity rol : usuario.getRoles()) {
			LOG.info("Rol: ".concat(rol.getRol()));
			roles.add(new SimpleGrantedAuthority(rol.getRol()));
		}
		
		if(roles.isEmpty()) {
			LOG.error("Error login: el usuario ' " + user +"' no tiene rol asignado!");
			throw new UsernameNotFoundException("El usuario " + user  + " no tiene rol asignado!");
		}

		return new User(usuario.getUsuario(), usuario.getContrasenia(), usuario.getEnabled(), true, true, true, roles);
	}

}
