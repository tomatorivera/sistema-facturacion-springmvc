package com.tocttaviano.crudclientes.app.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

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
	public String listar(Model model) {
		model.addAttribute("clientes", clienteService.listar());
		return "index";
	}
	
	@GetMapping("/crear")
	public String crear(Model model) {
		model.addAttribute("cliente", new Cliente());
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
		return "clienteForm";
	}
	
	@PostMapping("/guardar")
	public String guardar(@Valid Cliente cliente, BindingResult result, SessionStatus status) {
		logger.info("Cliente entrante: " + cliente);
		if(result.hasErrors())
			return "clienteForm";
		
		try {
			clienteService.guardar(cliente);
			status.setComplete();
			return "redirect:/index";
		} catch (Exception e) {
			logger.error("Error al guardar el cliente: " + e.getMessage());
			return "clienteForm";
		}
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para eliminación.");
			return "redirect:/index";
		}
		
		try {
			clienteService.eliminar(id);
			return "redirect:/index";
		} catch (Exception e) {
			logger.error("Error al eliminar el cliente: " + e.getMessage());
			return "redirect:/index";
		}
	}
}
