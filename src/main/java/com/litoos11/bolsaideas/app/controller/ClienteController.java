package com.litoos11.bolsaideas.app.controller;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.litoos11.bolsaideas.app.component.PaginadorComponent;
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
	public ModelAndView listar(@RequestParam(name="page", defaultValue="0") int page) {
		//cuantas elementos por paginas mostramos
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<ClienteEntity> clientes = clienteService.findAll(pageRequest);
		
		PaginadorComponent<ClienteEntity> paginadorComponent = new PaginadorComponent<>("/clientes/list", clientes);
		
		ModelAndView mav = new ModelAndView(CLIENTES_VIEW);
		mav.addObject("titulo", "Clientes");
		mav.addObject("clientes", clientes);
		mav.addObject("page", paginadorComponent);
		return mav;
	}


	@PostMapping("/save")
	public ModelAndView guardar(@Valid @ModelAttribute("cliente") ClienteEntity cliente, RedirectAttributes flash,
			BindingResult result, SessionStatus status) {
		ModelAndView mav = new ModelAndView();
		if (result.hasErrors()) {
			// LOG.info("Object client: " + cliente.getNombre());
			mav.setViewName(FORM_CLIENTE_VIEW);
			mav.addObject("titulo", "Alta de Cliente");
			return mav;
		}

		// Investigar como aprovechar el cliente que devuelve
		String msgFlash = (null != cliente.getId()) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
		
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", msgFlash);
		mav.setViewName("redirect:/clientes/list");
		return mav;
	}

	@GetMapping("/form")
	public ModelAndView crearOrEditar(@RequestParam(name = "id", required = false) Long id, RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView(FORM_CLIENTE_VIEW);
		ClienteEntity cliente = new ClienteEntity();
		if (id != 0) {
			cliente = clienteService.findById(id);
			if (null != cliente) {
				mav.addObject("titulo", "Editar Cliente");
			} else {
				flash.addFlashAttribute("error", "No existe el cliente!");
				mav.setViewName("redirect:/clientes/list");
			}

		} else {
			mav.addObject("titulo", "Alta de Cliente");
		}
		mav.addObject("cliente", cliente);
		return mav;
	}

	@GetMapping("/delete")
	public ModelAndView eliminar(@RequestParam(name = "id", required = true) Long id, RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView("redirect:/clientes/list");
		if (id != 0) {
			clienteService.deleteById(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");
		}
		return mav;
	}
}
