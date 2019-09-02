package com.litoos11.bolsaideas.app.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	private static final String FORM_LOGIN_VIEW = "login";

	@GetMapping("/login")
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, 
			Principal principal,
			RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView(FORM_LOGIN_VIEW);

		if (principal != null) {
			flash.addFlashAttribute("info", "Ya ha iniciado sesión!");
			mav.setViewName("redirect:/cliente/");
		}

		if (error != null) {
			mav.addObject("error", "Error de login: Usuario o contraseña incorrecta");
		}

		if (logout != null) {
			mav.addObject("success", "Sesión terminada con éxito!");
		}

		return mav;
	}

}
