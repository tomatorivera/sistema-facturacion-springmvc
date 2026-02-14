package com.tocttaviano.crudclientes.app.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	@GetMapping("/cargar-productos/{busqueda}")
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String busqueda) {
		return clienteService.buscarProductoPorNombre(busqueda);
	}
	
	@PostMapping("/guardar")
	public String guardar(
			@Valid Factura factura, 
			BindingResult resultadoValidacion,
			Model model,
			@RequestParam(name = "id_item[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes mensajeria,
			SessionStatus sessionStatus) 
	{
//		logger.info("Guardando factura - ITEM ID -> ", itemId);
//		logger.info("Guardando factura - CANTIDADES -> ", cantidad);
		logger.info("Guardando factura - DATOS FACTURA -> ", factura);
		
		if (resultadoValidacion.hasErrors()) {
			model.addAttribute("tituloPagina", "Crear factura");
			return "factura/facturaForm";
		}
		
		if (itemId == null || itemId.length == 0) {
			model.addAttribute("tituloPagina", "Crear factura");
			model.addAttribute("mensajeError", "La factura debe tener al menos un producto");
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
		
		mensajeria.addFlashAttribute("mensajeExito", "Factura creada con éxito");
		return "redirect:/listar";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(@PathVariable Long id, Model model, RedirectAttributes mensajeria) {
		Optional<Factura> optFactura = clienteService.buscarFacturaPorId(id);
		if (optFactura.isEmpty()) {
			mensajeria.addFlashAttribute("mensajeError", "No se ha encontrado la factura especificada");
			return "redirect:/listar";
		}
		
		model.addAttribute("tituloPagina", "Detalle de factura");
		model.addAttribute("factura", optFactura.get());
		
		return "factura/detalle";
		
	}
	
}
