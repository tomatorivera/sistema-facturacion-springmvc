package com.tocttaviano.crudclientes.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.repositories.IClienteRepository;

@Service
public class ClienteServiceImpl implements IClienteService {
	
	private IClienteRepository clienteRepository;
	
	public ClienteServiceImpl(IClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}
	
	@Override
	public List<Cliente> listar() {
		return (List<Cliente>) clienteRepository.findAll();
	}

	@Override
	public Cliente guardar(Cliente cliente) {
		return clienteRepository.save(cliente);
	}
	
	@Override
	public Optional<Cliente> buscarPorId(Long id) {
		return clienteRepository.findById(id);
	}

}
