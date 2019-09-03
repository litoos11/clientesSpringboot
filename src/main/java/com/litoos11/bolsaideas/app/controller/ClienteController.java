package com.litoos11.bolsaideas.app.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.litoos11.bolsaideas.app.component.PaginadorComponent;
import com.litoos11.bolsaideas.app.entity.ClienteEntity;
import com.litoos11.bolsaideas.app.service.IClienteService;
import com.litoos11.bolsaideas.app.service.IUploadFileService;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

	private static final String CLIENTES_VIEW = "clientes";
	private static final String FORM_CLIENTE_VIEW = "form";
	private static final String VER_VIEW = "ver";

	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Autowired
	@Qualifier("clienteServiceImpl")
	private IClienteService clienteService;

	@Autowired
	@Qualifier("uploadFileServiceImpl")
	private IUploadFileService uploadFileService;

	@GetMapping("/ver")
	public ModelAndView ver(@RequestParam(name = "id") Long id, RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView(VER_VIEW);

		// ClienteEntity cliente = clienteService.findById(id);
		ClienteEntity cliente = clienteService.fetchClienteByIdWithFacturas(id);
		if (cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la DB");
			mav.setViewName("redirect:/cliente/list");
		}

		mav.addObject("cliente", cliente);
		mav.addObject("titulo", "Detalle cliente: " + cliente.getNombre());

		return mav;
	}

	//@Secured("ROLE_USER") ó el siguiente
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping({ "/list", "/" })
	public ModelAndView listar(@RequestParam(name = "page", defaultValue = "0") int page, Authentication authentication,
			HttpServletRequest request) {
		if (authentication != null) {
			LOG.info("Hola usuario, tu user name es: ".concat(authentication.getName()));
		}

		// Validando roles de usuarios con nuestro propio metodo "hasRole()"
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (hasRole("ROLE_ADMIN")) {
			LOG.info("Hola: ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			LOG.info("Hola: ".concat(auth.getName()).concat(" no tienes acceso!"));
		}

		// validando roles de usuarios con clase
		// "SecurityContextHolderAwareRequestWrapper"
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,
				"ROLE_");

		if (securityContext.isUserInRole("ADMIN")) {
			LOG.info("Con clase SecurityContextHolderAwareRequestWrapper ---> Hola: ".concat(auth.getName())
					.concat(" tienes acceso!"));
		} else {
			LOG.info("Con clase SecurityContextHolderAwareRequestWrapper --->  Hola: ".concat(auth.getName())
					.concat(" no tienes acceso!"));
		}

		// Validando roles de usuarios con el objeto request
		if (request.isUserInRole("ROLE_ADMIN")) {
			LOG.info("Con objeto request --> Hola: ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			LOG.info("Con objeto request --> Hola: ".concat(auth.getName()).concat(" no tienes acceso!"));
		}
		// cuantas elementos por paginas mostramos
		Pageable pageRequest = PageRequest.of(page, 3);

		Page<ClienteEntity> clientes = clienteService.findAll(pageRequest);

		PaginadorComponent<ClienteEntity> paginadorComponent = new PaginadorComponent<>("/cliente/list", clientes);

		ModelAndView mav = new ModelAndView(CLIENTES_VIEW);
		mav.addObject("titulo", "Clientes");
		mav.addObject("clientes", clientes);
		mav.addObject("page", paginadorComponent);
		return mav;
	}

	//@Secured("ROLE_ADMIN") ó
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PostMapping("/save") 
	public ModelAndView guardar(@Valid @ModelAttribute("cliente") ClienteEntity cliente, BindingResult result,
			RedirectAttributes flash, SessionStatus status, @RequestParam(name = "file") MultipartFile foto) {
		ModelAndView mav = new ModelAndView();
		if (result.hasErrors()) {
			// LOG.info("Object client: " + cliente.getNombre());
			mav.setViewName(FORM_CLIENTE_VIEW);
			mav.addObject("titulo", "Alta de Cliente");
			return mav;
		}
		// LOG.info("ID: " + cliente.getId() + " Foto: " + cliente.getFoto() + " NOMBRE:
		// " +cliente.getNombre());

		if (cliente.getId() != null) {
			ClienteEntity antiguoCliente = clienteService.findById(cliente.getId());
			cliente.setFoto(antiguoCliente.getFoto());
		}

		LOG.info("ID: " + cliente.getId() + "     Foto: " + cliente.getFoto());
		if (!foto.isEmpty()) {
			if (cliente.getId() != null && cliente.getId() > 0) {

				uploadFileService.delete(cliente.getFoto());

			}

			String uniqueFileName = null;
			try {
				uniqueFileName = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Ha subidó correctamente '" + uniqueFileName + "'");
			cliente.setFoto(uniqueFileName);

		}

		// Investigar como aprovechar el cliente que devuelve
		String msgFlash = (null != cliente.getId()) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", msgFlash);
		mav.setViewName("redirect:/cliente/list");
		return mav;
	}

	@Secured("ROLE_ADMIN")
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
				mav.setViewName("redirect:/cliente/list");
			}

		} else {
			mav.addObject("titulo", "Alta de Cliente");
		}
		mav.addObject("cliente", cliente);
		return mav;
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/delete")
	public ModelAndView eliminar(@RequestParam(name = "id", required = true) Long id, RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView("redirect:/cliente/list");
		if (id != 0) {
			ClienteEntity cliente = clienteService.findById(id);

			clienteService.deleteById(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminado con éxito!");
			}
		}
		return mav;
	}

	public boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();
		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));
		/*
		 * for(GrantedAuthority authority: authorities) {
		 * if(role.equals(authority.getAuthority())) {
		 * LOG.info("Hola usuario: ".concat(auth.getName()).concat(" tu rol es :").
		 * concat(authority.getAuthority())); return true; } }
		 * 
		 * return false;
		 */
	}
}
