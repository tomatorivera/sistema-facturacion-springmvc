package com.tocttaviano.crudclientes.app.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.models.Factura;
import com.tocttaviano.crudclientes.app.models.Producto;

public interface IClienteService {
	List<Cliente> listar();
	Page<Cliente> listarPaginado(int numeroPagina, int tamanioPagina);
	Cliente guardar(Cliente cliente, MultipartFile foto) throws IOException;
	Optional<Cliente> buscarPorId(Long id);
	void eliminar(Cliente cliente) throws IOException;
	
	List<Producto> buscarProductoPorNombre(String nombre);
	Optional<Producto> buscarProductoPorId(Long id);
	
	void guardarFactura(Factura factura);
	Optional<Factura> buscarFacturaPorId(Long id);
}
