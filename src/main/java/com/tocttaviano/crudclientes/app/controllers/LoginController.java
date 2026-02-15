package com.tocttaviano.crudclientes.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(
			Model model, 
			Principal principal, 
			RedirectAttributes mensajeria,
			@RequestParam(required = false) String error
	){
		// Si el usuario ya ha iniciado sesión, redirige a la página de inicio
		if (principal != null) {
			mensajeria.addFlashAttribute("mensajeError", "Ya has iniciado sesión anteriormente");
			return "redirect:/";
		}
		
		// Si se ha producido un error de autenticación, muestra un mensaje de error
		if (error != null) {
			model.addAttribute("mensajeError", "Credenciales incorrectas. Inténtalo de nuevo.");
		}
		
		return "auth/login";
	}
	
}
