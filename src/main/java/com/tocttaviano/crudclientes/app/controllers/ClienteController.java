package com.tocttaviano.crudclientes.app.controllers;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private final IClienteService clienteService;
	private final Logger logger;
	
	@Autowired
	private MessageSource messageSource;
	
	public ClienteController(IClienteService clienteService) {
		this.clienteService = clienteService;
		this.logger = LoggerFactory.getLogger(ClienteController.class);
	}
	 
	@GetMapping({"/", "/index", "/listar"})
	public String listar(
			@RequestParam(defaultValue="0") int numeroPagina, 
			@RequestParam(defaultValue="5") int tamanioPagina,
			Model model,
			Locale locale) 
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			logger.info("Usuario autenticado: " + auth.getName());
		}
		
		Page<Cliente> paginaClientes = clienteService.listarPaginado(numeroPagina, tamanioPagina); // <- Obtengo la página
		
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", paginaClientes);
		return "index";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/detalle/{id}")
	public String detalle(
			@PathVariable Long id, 
			Model model, 
			RedirectAttributes mensajeria,
			Locale locale
	) {
		Optional<Cliente> optCliente = clienteService.buscarPorIdConFacturas(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para detalle.");
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.cliente.error.noEncontrado", null, locale));
			return "redirect:/index";
		}
		
		Cliente cliente = optCliente.get();
		model.addAttribute("clienteDetalle", cliente);
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.cliente.detalle.titulo", null, locale));
		return "detalle";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/guardar")
	public String crear(Model model, Locale locale, HttpSession session, SessionStatus sessionStatus) {
		// Al cambiar el idioma, puede realizarse una petición sin el ID del cliente
		// Si el cliente permanece en la sesión, quiere decir que se lo estaba editando
		// y en dado caso hago un forward a la URL correspondiente para continuar la operación
		Cliente clienteActual = (Cliente) session.getAttribute("cliente");
		if (!sessionStatus.isComplete() && clienteActual != null && (clienteActual.getId() != null && clienteActual.getId() > 0))
		{
			return "forward:/guardar/".concat(String.valueOf(clienteActual.getId()));
		}
			
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.cliente.crear.titulo", null, locale));
		return "clienteForm";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/guardar/{id}")
	public String editar(@PathVariable Long id, Model model, Locale locale) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para edición.");
			return "redirect:/index";
		}
		
		Cliente cliente = optCliente.get();
		logger.info("Cliente encontrado para edición: " + cliente);
		model.addAttribute("cliente", cliente);
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.cliente.editar.titulo", null, locale));
		return "clienteForm";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/guardar")
	public String guardar(
			@Valid Cliente cliente, 
			BindingResult result, 
			SessionStatus status, 
			Model model, 
			@RequestParam MultipartFile file,
			RedirectAttributes mensajeria,
			Locale locale) 
	{
		logger.info("Cliente entrante: " + cliente + " | Foto: " + (file != null ? file.getOriginalFilename() : "No se ha cargado una foto"));
		if(result.hasErrors()) {
			model.addAttribute("tituloPagina", (cliente.getId() != null && cliente.getId() > 0) 
					? messageSource.getMessage("Text.cliente.editar.titulo", null, locale)
					: messageSource.getMessage("Text.cliente.crear.titulo", null, locale));
			return "clienteForm";
		}
		
		try {
			mensajeria.addFlashAttribute("mensajeExito", (cliente.getId() != null && cliente.getId() > 0) 
					? messageSource.getMessage("Text.cliente.exito.editar", null, locale)
					: messageSource.getMessage("Text.cliente.exito.crear", null, locale));
			
			clienteService.guardar(cliente, file);
			status.setComplete();
			
			logger.info("Cliente guardado: " + cliente);
			return "redirect:/index";
		} catch (Exception e) {
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.cliente.error.guardado", null, locale));
			logger.error("Error al guardar el cliente: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/index";
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes mensajeria, Locale locale) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(id);
		if (optCliente.isEmpty()) {
			logger.warn("Cliente con ID " + id + " no encontrado para eliminación.");
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.cliente.error.noEncontrado", null, locale));
			return "redirect:/index";
		}
		
		try 
		{
			clienteService.eliminar(optCliente.get());
			mensajeria.addFlashAttribute("mensajeExito", messageSource.getMessage("Text.cliente.exito.eliminar", null, locale));	
		} 
		catch (IOException e) 
		{
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.cliente.error.eliminar", null, locale));
			logger.error("Error al eliminar el cliente: " + e.getMessage());
			e.printStackTrace();	
		}
		
		return "redirect:/index";
	}
}
