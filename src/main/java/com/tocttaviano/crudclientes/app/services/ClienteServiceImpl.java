package com.tocttaviano.crudclientes.app.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.models.Factura;
import com.tocttaviano.crudclientes.app.models.Producto;
import com.tocttaviano.crudclientes.app.repositories.IClienteRepository;
import com.tocttaviano.crudclientes.app.repositories.IFacturaRepository;
import com.tocttaviano.crudclientes.app.repositories.IProductoRepository;

@Service
public class ClienteServiceImpl implements IClienteService {

	private final IClienteRepository clienteRepository;
	private final IProductoRepository productoRepository;
	private final IFotoPerfilService fotoPerfilService;
	private final IFacturaRepository facturaRepository;
	
	public ClienteServiceImpl(
			IClienteRepository clienteRepository, 
			IProductoRepository productoRepository, 
			IFotoPerfilService fotoPerfilService, 
			IFacturaRepository facturaRepository) 
	{
		this.clienteRepository = clienteRepository;
		this.fotoPerfilService = fotoPerfilService;
		this.productoRepository = productoRepository;
		this.facturaRepository = facturaRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> listar() {
		return (List<Cliente>) clienteRepository.findAll();
	}

	@Override
	@Transactional
	public Cliente guardar(Cliente cliente, MultipartFile foto) throws IOException {
		if (!foto.isEmpty())
		{
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null)
				fotoPerfilService.eliminarFotoPerfil(cliente.getFoto());
			
			cliente.setFoto(fotoPerfilService.guardarFotoPerfil(foto));
		}
		
		return clienteRepository.save(cliente);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Cliente> buscarPorId(Long id) {
		return clienteRepository.findById(id);
	}
	
	@Override
	@Transactional
	public void eliminar(Cliente cliente) throws IOException {
		if (cliente.getFoto() != null)
			fotoPerfilService.eliminarFotoPerfil(cliente.getFoto());
		
		clienteRepository.deleteById(cliente.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> listarPaginado(int numeroPagina, int tamanioPagina) {
		Pageable pageable = PageRequest.of(numeroPagina, tamanioPagina);
		return clienteRepository.findAll(pageable);
	}
	
	@Override	
	@Transactional(readOnly = true)
	public List<Producto> buscarProductoPorNombre(String nombre) {
		return productoRepository.findByNombreContaining(nombre);
	}

	@Override
	@Transactional
	public void guardarFactura(Factura factura) {
		facturaRepository.save(factura);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Producto> buscarProductoPorId(Long id) {
		return productoRepository.findById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Factura> buscarFacturaPorId(Long id) {
		return facturaRepository.findById(id);
	}

}
