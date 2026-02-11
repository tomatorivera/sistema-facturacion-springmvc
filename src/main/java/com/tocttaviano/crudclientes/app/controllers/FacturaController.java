package com.tocttaviano.crudclientes.app.controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.models.Factura;
import com.tocttaviano.crudclientes.app.services.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	private final IClienteService clienteService;
	
	public FacturaController(IClienteService clienteService) {
		this.clienteService = clienteService;
	}
	
	@GetMapping("/crear/{idCliente}")
	public String crear(@PathVariable Long idCliente, Model model, RedirectAttributes mensajeria) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(idCliente);
		if (optCliente.isEmpty()) {
			mensajeria.addFlashAttribute("mensajeError", "No se ha encontrado al cliente especificado para crear la factura");
			return "redirect:/listar";
		}
		
		Factura factura = new Factura();
		factura.setCliente(optCliente.get());
		
		model.addAttribute("tituloPagina", "Crear factura");
		model.addAttribute("factura", factura);
		
		return "factura/facturaForm";
	}
	
}
