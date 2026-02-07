package com.tocttaviano.crudclientes.app.services;

import java.util.List;
import java.util.Optional;

import com.tocttaviano.crudclientes.app.models.Cliente;

public interface IClienteService {
	List<Cliente> listar();
	Cliente guardar(Cliente cliente);
	Optional<Cliente> buscarPorId(Long id);
}
