package com.tocttaviano.crudclientes.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tocttaviano.crudclientes.app.services.IClienteService;

@Controller
public class ClienteController {

	private final IClienteService clienteService;
	
	public ClienteController(IClienteService clienteService) {
		this.clienteService = clienteService;
	}
	
	@GetMapping({"/", "/index", "/listar"})
	public String listar(Model model) {
//		model.addAttribute("clientes", clienteService.listar());
		return "index";
	}
	
}
