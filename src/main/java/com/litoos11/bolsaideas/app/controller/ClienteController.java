package com.litoos11.bolsaideas.app.controller;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.service.IClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	private static final String CLIENTES_VIEW = "clientes";
	private static final String FORM_CLIENTE_VIEW = "form";

	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Autowired
	@Qualifier("clienteServiceImpl")
	private IClienteService clienteService;

	@GetMapping("/list")
	public ModelAndView listar() {
		ModelAndView mav = new ModelAndView(CLIENTES_VIEW);
		mav.addObject("titulo", "Clientes");
		mav.addObject("clientes", clienteService.findAll());
		return mav;
	}

	// @GetMapping("/form")
	// public ModelAndView crear() {
	// ClienteEntity cliente = new ClienteEntity();
	// ModelAndView mav = new ModelAndView(FORM_CLIENTE_VIEW);
	//
	// mav.addObject("cliente", cliente);
	// mav.addObject("titulo", "Alta de Cliente");
	// return mav;
	// }

	@PostMapping("/form")
	public ModelAndView guardar(@Valid @ModelAttribute("cliente") ClienteEntity cliente, BindingResult result) {
		ModelAndView mav = new ModelAndView();
		if (result.hasErrors()) {			
			LOG.info("Object client: " + cliente.getNombre());
			mav.setViewName(FORM_CLIENTE_VIEW);			
			mav.addObject("titulo", "Alta de Cliente");
			return mav;
		}

		//Investigar como aprovechar el cliente que devuelve
		clienteService.save(cliente);
		mav.setViewName("redirect:/clientes/list");
		return mav;
	}

	@GetMapping("/form")
	public ModelAndView crearOrEditar(@RequestParam(name = "id", required = false) Long id) {
		ModelAndView mav = new ModelAndView(FORM_CLIENTE_VIEW);
		ClienteEntity cliente = new ClienteEntity();
		if (id != 0) {
			cliente = clienteService.findById(id);
			mav.addObject("titulo", "Alta de Cliente");
			LOG.info("Client: " + cliente.getNombre());
		}
		mav.addObject("titulo", "Editar Cliente");
		mav.addObject("cliente", cliente);		
		return mav;
	}
}
