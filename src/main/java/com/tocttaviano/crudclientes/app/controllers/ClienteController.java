package com.tocttaviano.crudclientes.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

	@GetMapping({"/", "/index", "/listar"})
	public String listar(Model model) {
		return "index";
	}
	
}
