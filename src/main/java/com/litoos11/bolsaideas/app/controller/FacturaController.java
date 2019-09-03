package com.litoos11.bolsaideas.app.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.entity.FacturaEntity;
import com.litoos11.bolsaideas.app.entity.ItemFacturaEntity;
import com.litoos11.bolsaideas.app.entity.ProductoEntity;
import com.litoos11.bolsaideas.app.service.IClienteService;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	private static final String FORM_FACTURA_VIEW = "factura/form";
	private static final String VER_FACTURA_VIEW = "factura/ver";

	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Autowired
	@Qualifier("clienteServiceImpl")
	private IClienteService clienteService;

	@GetMapping("/ver/{id}")
	public ModelAndView ver(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		ModelAndView mav = new ModelAndView(VER_FACTURA_VIEW);
		//FacturaEntity factura = clienteService.findFacturaById(id);
		FacturaEntity factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id);

		if (factura == null) {
			flash.addFlashAttribute("error", "No existe la factura en la base de datos!");
			mav.setViewName("redirect:/cliente/list");
		}

		mav.addObject("factura", factura);
		mav.addObject("titulo", "Factura".concat(factura.getDescripcion()));
		return mav;
	}

	// formato localhots/factura/from?clienteid=1
	/*
	 * @GetMapping("/form") public ModelAndView crearOEditar(@RequestParam(name =
	 * "clienteid", required = true) Long clienteid,
	 */

	// formato localhots/factura/from/1
	@GetMapping("/form/{clienteid}")
	public ModelAndView crearOEditar(@PathVariable(value = "clienteid", required = true) Long clienteid,
			RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView(FORM_FACTURA_VIEW);

		LOG.info("ID: " + clienteid);

		ClienteEntity cliente = clienteService.findById(clienteid);

		if (cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la DB");
			mav.setViewName("redirect:/cliente/list");
		}

		LOG.info("Ciente: " + cliente);

		FacturaEntity factura = new FacturaEntity();
		factura.setCliente(cliente);

		mav.addObject("titulo", "Crear factura");
		mav.addObject("factura", factura);

		return mav;
	}

	@GetMapping(value = "/cargar-productos/{term}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ProductoEntity> cargarProductos(@PathVariable(value = "term") String term) {
		LOG.info("term");
		return clienteService.findByNombre(term);
	}

	@PostMapping("/save")
	public ModelAndView gurdar(@Valid @ModelAttribute("factura") FacturaEntity factura, 
			BindingResult result,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes flash,
			SessionStatus status) {	
		ModelAndView mav = new ModelAndView();
		
		if(result.hasErrors()) {
			mav.addObject("titulo", "Crear factura");
			mav.setViewName(FORM_FACTURA_VIEW);
			return mav;
		}
		
		if(itemId == null || itemId.length == 0) {
			mav.addObject("titulo", "Crear factura");
			//mav.setViewName("redirect:/factura/form");
			mav.setViewName(FORM_FACTURA_VIEW);
			mav.addObject("error", "La facturo debe tener al menos una lina");
			return mav;
		}
		
		for(int i = 0; i < itemId.length; i++) {
			ProductoEntity producto = clienteService.findProductoById(itemId[i]);
			
			ItemFacturaEntity linea = new ItemFacturaEntity();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			
			factura.addItemFactura(linea);
			
			LOG.info("ID: " + itemId[i].toString() + ",  CANTIDAD: " + cantidad[i].toString());
		}
		
		clienteService.saveFactura(factura);
		
		status.setComplete();
		flash.addFlashAttribute("success", "Factura creada con éxito!!!");
		
		mav.setViewName("redirect:/cliente/ver?id=" + factura.getCliente().getId());
		
		return mav;
		
	}

	@GetMapping(value="/delete/{id}")
	public ModelAndView eliminar(@PathVariable(value="id")Long id, RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView();
		
		FacturaEntity factura = clienteService.findFacturaById(id);
		
		if(factura != null) {
			clienteService.deleteFacturaById(id);
			flash.addFlashAttribute("success", "Factura eliminada con éxito!");
			mav.setViewName("redirect:/cliente/ver?id=" + factura.getCliente().getId());
		}else {
			flash.addAttribute("error", "No existe la factura en la base de datos!");
			mav.setViewName("redirect:/cliente/list");
		}		
		
		return mav;
	}
}
