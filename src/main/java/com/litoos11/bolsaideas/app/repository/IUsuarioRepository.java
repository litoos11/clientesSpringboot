package com.litoos11.bolsaideas.app.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.litoos11.bolsaideas.app.entity.UsuarioEntity;

@Repository("usuarioRepository")
public interface IUsuarioRepository extends  JpaRepository<UsuarioEntity, Serializable>{

	@Query("SELECT u FROM UsuarioEntity u WHERE u.usuario = ?1")
	public UsuarioEntity fetchByUsuario(String usuario);
	
	public UsuarioEntity findByUsuario(String usuario);
}
