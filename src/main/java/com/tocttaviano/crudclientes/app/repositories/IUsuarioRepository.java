package com.tocttaviano.crudclientes.app.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tocttaviano.crudclientes.app.models.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long> {
	
	public Usuario findByUsername(String username);
	
}
