package com.litoos11.bolsaideas.app.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.litoos11.bolsaideas.app.entity.ProductoEntity;

@Repository("productoRepository")
public interface IProductoRepository extends JpaRepository<ProductoEntity, Serializable>{
	
	@Query("Select p from ProductoEntity p where p.nombre like %?1%")
	public List<ProductoEntity> findByNombre(String term);
	
	public List<ProductoEntity> findByNombreLikeIgnoreCase(String term);
}
