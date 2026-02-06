package com.tocttaviano.crudclientes.app.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tocttaviano.crudclientes.app.models.Cliente;

public interface IClienteRepository extends CrudRepository<Cliente, Long> {

}
