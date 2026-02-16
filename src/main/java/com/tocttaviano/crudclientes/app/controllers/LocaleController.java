package com.tocttaviano.crudclientes.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LocaleController {

	/**
	 * Se encarga de guardar la última página consultada antes de
	 * realizar un cambio de idioma. Esto resulta útil en casos como el
	 * de la tabla paginada de clientes para no perder la página
	 * consultada al realizar el cambio de idioma.
	 * 
	 * @param request
	 * @return Redirección a la última página consultada antes de cambiar el idioma
	 */
	@GetMapping("/locale")
	public String locale(HttpServletRequest request) {
		String ultimaUrl = request.getHeader("referer");
		return "redirect:".concat(ultimaUrl);
	}
	
}
