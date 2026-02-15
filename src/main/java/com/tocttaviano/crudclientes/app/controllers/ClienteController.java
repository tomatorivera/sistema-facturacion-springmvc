package com.tocttaviano.crudclientes.app.controllers;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.services.IClienteService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private final IClienteService clienteService;
	private final Logger logger;
	
	public ClienteController(IClienteService clienteService) {
		this.clienteService = clienteService;
		this.logger = LoggerFactory.getLogger(ClienteController.class);
	}
	
	@GetMapping({"/", "/index", "/listar"})
	public String listar(
			@RequestParam(defaultValue="0") int numeroPagina, 
			@RequestParam(defaultValue="5") int tamanioPagina,
			Model model) 
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			logger.info("Usuario autenticado: " + auth.getName());
		}
		
		Page<Cliente> paginaClientes = clienteService.listarPaginado(numeroPagina, tamanioPagina); // <- Obtengo la página
		
		model.addAttribute("tituloPagina", "Listado de clientes");
		model.addAttribute("clientes", paginaClientes);
		return "index";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(@PathVariable Long id, Model model, RedirectAttributes mensajeria) {
		Optional<Cliente> optCliente = clienteService.buscarPorIdConFacturas(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para detalle.");
			mensajeria.addFlashAttribute("mensajeError", "No se ha encontrado al cliente especificado para mostrar su detalle");
			return "redirect:/index";
		}
		
		Cliente cliente = optCliente.get();
		model.addAttribute("cliente", cliente);
		model.addAttribute("tituloPagina", "Detalle del cliente");
		return "detalle";
	}
	
	@GetMapping("/crear")
	public String crear(Model model) {
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("tituloPagina", "Agregar cliente");
		return "clienteForm";
	}
	
	@GetMapping("/editar/{id}")
	public String editar(@PathVariable Long id, Model model) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para edición.");
			return "redirect:/index";
		}
		
		Cliente cliente = optCliente.get();
		logger.info("Cliente encontrado para edición: " + cliente);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tituloPagina", "Editar cliente");
		return "clienteForm";
	}
	
	@PostMapping("/guardar")
	public String guardar(
			@Valid Cliente cliente, 
			BindingResult result, 
			SessionStatus status, 
			Model model, 
			@RequestParam MultipartFile file,
			RedirectAttributes mensajeria) 
	{
		logger.info("Cliente entrante: " + cliente + " | Foto: " + (file != null ? file.getOriginalFilename() : "No se ha cargado una foto"));
		if(result.hasErrors()) {
			model.addAttribute("tituloPagina", (cliente.getId() != null && cliente.getId() > 0) ? "Editar cliente" : "Agregar cliente");
			return "clienteForm";
		}
		
		try {
			clienteService.guardar(cliente, file);
			
			mensajeria.addFlashAttribute("mensajeExito", (cliente.getId() != null && cliente.getId() > 0) ? "Cliente editado exitosamente" : "Cliente creado exitosamente");
			status.setComplete();
			
			logger.info("Cliente guardado: " + cliente);
			return "redirect:/index";
		} catch (Exception e) {
			mensajeria.addFlashAttribute("mensajeError", "Ha ocurrido un error de sistema guardando al cliente");
			logger.error("Error al guardar el cliente: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/index";
		}
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes mensajeria) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para eliminación.");
			mensajeria.addFlashAttribute("mensajeError", "No se ha encontrado al cliente especificado para eliminar");
			return "redirect:/index";
		}
		
		try 
		{
			clienteService.eliminar(optCliente.get());
			mensajeria.addFlashAttribute("mensajeExito", "Cliente eliminado exitosamente");	
		} 
		catch (IOException e) 
		{
			mensajeria.addFlashAttribute("mensajeError", "Ha ocurrido un error de sistema eliminando al cliente");
			logger.error("Error al eliminar el cliente: " + e.getMessage());
			e.printStackTrace();	
		}
		
		return "redirect:/index";
	}
}
