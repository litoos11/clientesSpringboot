package com.litoos11.bolsaideas.app.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	private static final String CLIENTES_VIEW = "clientes";
	private static final String FORM_CLIENTE_VIEW = "form";
	private static final String VER_VIEW = "ver";
	private static final String UPLOADS_FOLDER = "uploads";

	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Autowired
	@Qualifier("clienteServiceImpl")
	private IClienteService clienteService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		Path pathFoto = Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
		LOG.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("Error no se puede cargar la imagen: " + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename() +"\"")
				.body(recurso);
	}
	
	@GetMapping("/ver")
	public ModelAndView ver(@RequestParam(name = "id") Long id,RedirectAttributes flash) {
		ModelAndView mav = new ModelAndView(VER_VIEW);
		
		ClienteEntity cliente = clienteService.findById(id);
		if(cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la DB");
			mav.setViewName("redirect:/clientes/list");
		}
		
		mav.addObject("cliente", cliente);
		mav.addObject("titulo", "Detalle cliente: " + cliente.getNombre());
		
		return mav;
	}
	
	@GetMapping("/list")
	public ModelAndView listar(@RequestParam(name="page", defaultValue="0") int page) {
		//cuantas elementos por paginas mostramos
		Pageable pageRequest = PageRequest.of(page, 3);
		
		Page<ClienteEntity> clientes = clienteService.findAll(pageRequest);
		
		PaginadorComponent<ClienteEntity> paginadorComponent = new PaginadorComponent<>("/clientes/list", clientes);
		
		ModelAndView mav = new ModelAndView(CLIENTES_VIEW);
		mav.addObject("titulo", "Clientes");
		mav.addObject("clientes", clientes);
		mav.addObject("page", paginadorComponent);
		return mav;
	}


	@PostMapping("/save")
	public ModelAndView guardar(@Valid @ModelAttribute("cliente") ClienteEntity cliente,
			@RequestParam(name = "file") MultipartFile foto,
			RedirectAttributes flash,
			BindingResult result, SessionStatus status) {
		ModelAndView mav = new ModelAndView();
		if (result.hasErrors()) {
			// LOG.info("Object client: " + cliente.getNombre());
			mav.setViewName(FORM_CLIENTE_VIEW);
			mav.addObject("titulo", "Alta de Cliente");
			return mav;
		}

		ClienteEntity antiguoCliente = clienteService.findById(cliente.getId());
		if(!foto.isEmpty()) {
			//Path dirRecursos = Paths.get("src/main/resources/static/uploads");
			//String rootPath = dirRecursos.toFile().getAbsolutePath();
			//String rootPath = "C://temp//uploads";
			LOG.info("ID: " + antiguoCliente.getId() + "     Foto: " + antiguoCliente.getFoto());
			if(antiguoCliente.getId() != null
					&& antiguoCliente.getId() > 0
					&& antiguoCliente.getFoto() != null
					&& antiguoCliente.getFoto().length() > 0) {
				
				Path rootPathOld = Paths.get(UPLOADS_FOLDER).resolve(antiguoCliente.getFoto()).toAbsolutePath();
				File archivo = rootPathOld.toFile();
				LOG.info("rootPathOld: " + rootPathOld);
				if(archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}
			
			String uniqueFileName = UUID.randomUUID().toString() + " " + foto.getOriginalFilename();
			Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(uniqueFileName);
			Path rootAbsolutePath = rootPath.toAbsolutePath();
			LOG.info("rootPath: " + rootPath);
			LOG.info("rootAbsolutePath: " + rootAbsolutePath);
			try {
//				byte[] bytes = foto.getBytes();
//				Path rutaCompleta = Paths.get(rootPath + "//" + foto.getOriginalFilename());
//				Files.write(rutaCompleta, bytes);
				
				Files.copy(foto.getInputStream(), rootAbsolutePath);
				flash.addAttribute("info", "Ha subidó correctamente '" + uniqueFileName +"'");
				
				cliente.setFoto(uniqueFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			ClienteEntity cliente = clienteService.findById(id);
			
			clienteService.deleteById(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito!");
			
			Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(cliente.getFoto()).toAbsolutePath();
			File archivo = rootPath.toFile();
			
			if(archivo.exists() && archivo.canRead()) {
				if(archivo.delete()) {
					flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminado con éxito!");
				}
			}
		}
		return mav;
	}
}
