package com.tocttaviano.crudclientes.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tocttaviano.crudclientes.app.models.Cliente;
import com.tocttaviano.crudclientes.app.models.Factura;
import com.tocttaviano.crudclientes.app.models.ItemFactura;
import com.tocttaviano.crudclientes.app.models.Producto;
import com.tocttaviano.crudclientes.app.services.IClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	private final IClienteService clienteService;
	private final Logger logger = LoggerFactory.getLogger(FacturaController.class);
	
	@Autowired
	private MessageSource messageSource;
	
	public FacturaController(IClienteService clienteService) {
		this.clienteService = clienteService;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/crear/{idCliente}")
	public String crear(@PathVariable Long idCliente, Model model, RedirectAttributes mensajeria, Locale locale) {
		Optional<Cliente> optCliente = clienteService.buscarPorId(idCliente);
		if (optCliente.isEmpty()) {
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.cliente.error.noEncontrado", null, locale));
			return "redirect:/listar";
		}
		
		Factura factura = new Factura();
		factura.setCliente(optCliente.get());
		
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.factura.crear.titulo", null, locale));
		model.addAttribute("factura", factura);
		
		return "factura/facturaForm";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/cargar-productos/{busqueda}")
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String busqueda) {
		return clienteService.buscarProductoPorNombre(busqueda);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/guardar")
	public String guardar(
			@Valid Factura factura, 
			BindingResult resultadoValidacion,
			Model model,
			@RequestParam(name = "id_item[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes mensajeria,
			SessionStatus sessionStatus,
			Locale locale
	) {
		logger.info("Guardando factura - DATOS FACTURA -> ", factura);
		
		if (resultadoValidacion.hasErrors()) {
			model.addAttribute("tituloPagina", messageSource.getMessage("Text.factura.crear.titulo", null, locale));
			return "factura/facturaForm";
		}
		
		if (itemId == null || itemId.length == 0) {
			model.addAttribute("tituloPagina", messageSource.getMessage("Text.factura.crear.titulo", null, locale));
			model.addAttribute("mensajeError", messageSource.getMessage("Text.factura.error.sinProductos", null, locale));
			return "factura/facturaForm";
		}
		
		Producto producto = null;
		for (int i = 0; i < itemId.length; i++) {
			logger.info("Guardando factura - ITEM ID -> " + itemId[i]);
			logger.info("Guardando factura - CANTIDAD -> " + cantidad[i]);
			
			producto = clienteService.buscarProductoPorId(itemId[i]).orElse(null);
			if (producto != null) {
				logger.info("Guardando factura - PRODUCTO ENCONTRADO -> " + producto.getNombre());
			} else {
				logger.warn("Guardando factura - PRODUCTO NO ENCONTRADO PARA ID -> " + itemId[i]);
			}
			
			factura.addItemFactura(new ItemFactura(producto, cantidad[i]));
		}
		
		clienteService.guardarFactura(factura);
		sessionStatus.setComplete();
		
		mensajeria.addFlashAttribute("mensajeExito", messageSource.getMessage("Text.factura.exito.crear", null, locale));
		return "redirect:/listar";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/detalle/{id}")
	public String detalle(@PathVariable Long id, Model model, RedirectAttributes mensajeria, Locale locale) {
		Optional<Factura> optFactura = clienteService.buscarFacturaPorId(id);
		if (optFactura.isEmpty()) {
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.factura.error.noEncontrado", null, locale));
			return "redirect:/listar";
		}
		
		model.addAttribute("tituloPagina", messageSource.getMessage("Text.factura.detalle.titulo", null, locale));
		model.addAttribute("factura", optFactura.get());
		
		return "factura/detalle";
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes mensajeria, Locale locale) {
		Optional<Factura> optFactura = clienteService.buscarFacturaPorId(id);
		if (optFactura.isEmpty()) {
			mensajeria.addFlashAttribute("mensajeError", messageSource.getMessage("Text.factura.error.noEncontrado", null, locale));
			return "redirect:/listar";
		}
		
		clienteService.eliminarFactura(id);
		
		mensajeria.addFlashAttribute("mensajeExito", messageSource.getMessage("Text.factura.exito.eliminar", null, locale));
		return "redirect:/listar";
	}
	
}
